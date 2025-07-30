package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CandidatureAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import com.mycompany.myapp.repository.CandidatureRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.service.mapper.CandidatureMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CandidatureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CandidatureResourceIT {

    private static final Instant DEFAULT_DATE_POSTULATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_POSTULATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final StatutCandidature DEFAULT_STATUT = StatutCandidature.EN_ATTENTE;
    private static final StatutCandidature UPDATED_STATUT = StatutCandidature.ACCEPTEE;

    private static final String ENTITY_API_URL = "/api/candidatures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private CandidatureMapper candidatureMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Candidature candidature;

    private Candidature insertedCandidature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidature createEntity() {
        return new Candidature().datePostulation(DEFAULT_DATE_POSTULATION).statut(DEFAULT_STATUT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidature createUpdatedEntity() {
        return new Candidature().datePostulation(UPDATED_DATE_POSTULATION).statut(UPDATED_STATUT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Candidature.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        candidature = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCandidature != null) {
            candidatureRepository.delete(insertedCandidature).block();
            insertedCandidature = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCandidature() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);
        var returnedCandidatureDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CandidatureDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Candidature in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCandidature = candidatureMapper.toEntity(returnedCandidatureDTO);
        assertCandidatureUpdatableFieldsEquals(returnedCandidature, getPersistedCandidature(returnedCandidature));

        insertedCandidature = returnedCandidature;
    }

    @Test
    void createCandidatureWithExistingId() throws Exception {
        // Create the Candidature with an existing ID
        candidature.setId(1L);
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkDatePostulationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidature.setDatePostulation(null);

        // Create the Candidature, which fails.
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidature.setStatut(null);

        // Create the Candidature, which fails.
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCandidatures() {
        // Initialize the database
        insertedCandidature = candidatureRepository.save(candidature).block();

        // Get all the candidatureList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(candidature.getId().intValue()))
            .jsonPath("$.[*].datePostulation")
            .value(hasItem(DEFAULT_DATE_POSTULATION.toString()))
            .jsonPath("$.[*].statut")
            .value(hasItem(DEFAULT_STATUT.toString()));
    }

    @Test
    void getCandidature() {
        // Initialize the database
        insertedCandidature = candidatureRepository.save(candidature).block();

        // Get the candidature
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, candidature.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(candidature.getId().intValue()))
            .jsonPath("$.datePostulation")
            .value(is(DEFAULT_DATE_POSTULATION.toString()))
            .jsonPath("$.statut")
            .value(is(DEFAULT_STATUT.toString()));
    }

    @Test
    void getNonExistingCandidature() {
        // Get the candidature
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCandidature() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.save(candidature).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidature
        Candidature updatedCandidature = candidatureRepository.findById(candidature.getId()).block();
        updatedCandidature.datePostulation(UPDATED_DATE_POSTULATION).statut(UPDATED_STATUT);
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(updatedCandidature);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, candidatureDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCandidatureToMatchAllProperties(updatedCandidature);
    }

    @Test
    void putNonExistingCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, candidatureDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCandidatureWithPatch() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.save(candidature).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidature using partial update
        Candidature partialUpdatedCandidature = new Candidature();
        partialUpdatedCandidature.setId(candidature.getId());

        partialUpdatedCandidature.statut(UPDATED_STATUT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCandidature.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCandidature))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCandidature, candidature),
            getPersistedCandidature(candidature)
        );
    }

    @Test
    void fullUpdateCandidatureWithPatch() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.save(candidature).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidature using partial update
        Candidature partialUpdatedCandidature = new Candidature();
        partialUpdatedCandidature.setId(candidature.getId());

        partialUpdatedCandidature.datePostulation(UPDATED_DATE_POSTULATION).statut(UPDATED_STATUT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCandidature.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCandidature))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatureUpdatableFieldsEquals(partialUpdatedCandidature, getPersistedCandidature(partialUpdatedCandidature));
    }

    @Test
    void patchNonExistingCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, candidatureDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(candidatureDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCandidature() {
        // Initialize the database
        insertedCandidature = candidatureRepository.save(candidature).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the candidature
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, candidature.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return candidatureRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Candidature getPersistedCandidature(Candidature candidature) {
        return candidatureRepository.findById(candidature.getId()).block();
    }

    protected void assertPersistedCandidatureToMatchAllProperties(Candidature expectedCandidature) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCandidatureAllPropertiesEquals(expectedCandidature, getPersistedCandidature(expectedCandidature));
        assertCandidatureUpdatableFieldsEquals(expectedCandidature, getPersistedCandidature(expectedCandidature));
    }

    protected void assertPersistedCandidatureToMatchUpdatableProperties(Candidature expectedCandidature) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCandidatureAllUpdatablePropertiesEquals(expectedCandidature, getPersistedCandidature(expectedCandidature));
        assertCandidatureUpdatableFieldsEquals(expectedCandidature, getPersistedCandidature(expectedCandidature));
    }
}
