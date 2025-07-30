package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.OffreEmploiAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.mapper.OffreEmploiMapper;
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
 * Integration tests for the {@link OffreEmploiResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OffreEmploiResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOCALISATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCALISATION = "BBBBBBBBBB";

    private static final Double DEFAULT_SALAIRE = 1D;
    private static final Double UPDATED_SALAIRE = 2D;

    private static final Instant DEFAULT_DATE_PUBLICATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_PUBLICATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_EXPIRATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_EXPIRATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/offre-emplois";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OffreEmploiRepository offreEmploiRepository;

    @Autowired
    private OffreEmploiMapper offreEmploiMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OffreEmploi offreEmploi;

    private OffreEmploi insertedOffreEmploi;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OffreEmploi createEntity() {
        return new OffreEmploi()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .localisation(DEFAULT_LOCALISATION)
            .salaire(DEFAULT_SALAIRE)
            .datePublication(DEFAULT_DATE_PUBLICATION)
            .dateExpiration(DEFAULT_DATE_EXPIRATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OffreEmploi createUpdatedEntity() {
        return new OffreEmploi()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .localisation(UPDATED_LOCALISATION)
            .salaire(UPDATED_SALAIRE)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateExpiration(UPDATED_DATE_EXPIRATION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OffreEmploi.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        offreEmploi = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedOffreEmploi != null) {
            offreEmploiRepository.delete(insertedOffreEmploi).block();
            insertedOffreEmploi = null;
        }
        deleteEntities(em);
    }

    @Test
    void createOffreEmploi() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);
        var returnedOffreEmploiDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(OffreEmploiDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the OffreEmploi in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOffreEmploi = offreEmploiMapper.toEntity(returnedOffreEmploiDTO);
        assertOffreEmploiUpdatableFieldsEquals(returnedOffreEmploi, getPersistedOffreEmploi(returnedOffreEmploi));

        insertedOffreEmploi = returnedOffreEmploi;
    }

    @Test
    void createOffreEmploiWithExistingId() throws Exception {
        // Create the OffreEmploi with an existing ID
        offreEmploi.setId(1L);
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offreEmploi.setTitre(null);

        // Create the OffreEmploi, which fails.
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offreEmploi.setDescription(null);

        // Create the OffreEmploi, which fails.
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLocalisationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offreEmploi.setLocalisation(null);

        // Create the OffreEmploi, which fails.
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDatePublicationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        offreEmploi.setDatePublication(null);

        // Create the OffreEmploi, which fails.
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllOffreEmplois() {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.save(offreEmploi).block();

        // Get all the offreEmploiList
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
            .value(hasItem(offreEmploi.getId().intValue()))
            .jsonPath("$.[*].titre")
            .value(hasItem(DEFAULT_TITRE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].localisation")
            .value(hasItem(DEFAULT_LOCALISATION))
            .jsonPath("$.[*].salaire")
            .value(hasItem(DEFAULT_SALAIRE))
            .jsonPath("$.[*].datePublication")
            .value(hasItem(DEFAULT_DATE_PUBLICATION.toString()))
            .jsonPath("$.[*].dateExpiration")
            .value(hasItem(DEFAULT_DATE_EXPIRATION.toString()));
    }

    @Test
    void getOffreEmploi() {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.save(offreEmploi).block();

        // Get the offreEmploi
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, offreEmploi.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(offreEmploi.getId().intValue()))
            .jsonPath("$.titre")
            .value(is(DEFAULT_TITRE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.localisation")
            .value(is(DEFAULT_LOCALISATION))
            .jsonPath("$.salaire")
            .value(is(DEFAULT_SALAIRE))
            .jsonPath("$.datePublication")
            .value(is(DEFAULT_DATE_PUBLICATION.toString()))
            .jsonPath("$.dateExpiration")
            .value(is(DEFAULT_DATE_EXPIRATION.toString()));
    }

    @Test
    void getNonExistingOffreEmploi() {
        // Get the offreEmploi
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingOffreEmploi() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.save(offreEmploi).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offreEmploi
        OffreEmploi updatedOffreEmploi = offreEmploiRepository.findById(offreEmploi.getId()).block();
        updatedOffreEmploi
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .localisation(UPDATED_LOCALISATION)
            .salaire(UPDATED_SALAIRE)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateExpiration(UPDATED_DATE_EXPIRATION);
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(updatedOffreEmploi);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offreEmploiDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOffreEmploiToMatchAllProperties(updatedOffreEmploi);
    }

    @Test
    void putNonExistingOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, offreEmploiDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOffreEmploiWithPatch() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.save(offreEmploi).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offreEmploi using partial update
        OffreEmploi partialUpdatedOffreEmploi = new OffreEmploi();
        partialUpdatedOffreEmploi.setId(offreEmploi.getId());

        partialUpdatedOffreEmploi
            .localisation(UPDATED_LOCALISATION)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateExpiration(UPDATED_DATE_EXPIRATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOffreEmploi.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOffreEmploi))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OffreEmploi in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOffreEmploiUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOffreEmploi, offreEmploi),
            getPersistedOffreEmploi(offreEmploi)
        );
    }

    @Test
    void fullUpdateOffreEmploiWithPatch() throws Exception {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.save(offreEmploi).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the offreEmploi using partial update
        OffreEmploi partialUpdatedOffreEmploi = new OffreEmploi();
        partialUpdatedOffreEmploi.setId(offreEmploi.getId());

        partialUpdatedOffreEmploi
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .localisation(UPDATED_LOCALISATION)
            .salaire(UPDATED_SALAIRE)
            .datePublication(UPDATED_DATE_PUBLICATION)
            .dateExpiration(UPDATED_DATE_EXPIRATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOffreEmploi.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedOffreEmploi))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OffreEmploi in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOffreEmploiUpdatableFieldsEquals(partialUpdatedOffreEmploi, getPersistedOffreEmploi(partialUpdatedOffreEmploi));
    }

    @Test
    void patchNonExistingOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, offreEmploiDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOffreEmploi() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        offreEmploi.setId(longCount.incrementAndGet());

        // Create the OffreEmploi
        OffreEmploiDTO offreEmploiDTO = offreEmploiMapper.toDto(offreEmploi);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(offreEmploiDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OffreEmploi in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOffreEmploi() {
        // Initialize the database
        insertedOffreEmploi = offreEmploiRepository.save(offreEmploi).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the offreEmploi
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, offreEmploi.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return offreEmploiRepository.count().block();
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

    protected OffreEmploi getPersistedOffreEmploi(OffreEmploi offreEmploi) {
        return offreEmploiRepository.findById(offreEmploi.getId()).block();
    }

    protected void assertPersistedOffreEmploiToMatchAllProperties(OffreEmploi expectedOffreEmploi) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOffreEmploiAllPropertiesEquals(expectedOffreEmploi, getPersistedOffreEmploi(expectedOffreEmploi));
        assertOffreEmploiUpdatableFieldsEquals(expectedOffreEmploi, getPersistedOffreEmploi(expectedOffreEmploi));
    }

    protected void assertPersistedOffreEmploiToMatchUpdatableProperties(OffreEmploi expectedOffreEmploi) {
        // Test fails because reactive api returns an empty object instead of null
        // assertOffreEmploiAllUpdatablePropertiesEquals(expectedOffreEmploi, getPersistedOffreEmploi(expectedOffreEmploi));
        assertOffreEmploiUpdatableFieldsEquals(expectedOffreEmploi, getPersistedOffreEmploi(expectedOffreEmploi));
    }
}
