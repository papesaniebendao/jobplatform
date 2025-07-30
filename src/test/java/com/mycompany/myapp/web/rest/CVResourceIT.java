package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CVAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.CV;
import com.mycompany.myapp.repository.CVRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.CVDTO;
import com.mycompany.myapp.service.mapper.CVMapper;
import java.time.Duration;
import java.util.List;
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
 * Integration tests for the {@link CVResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CVResourceIT {

    private static final String DEFAULT_URL_FICHIER = "AAAAAAAAAA";
    private static final String UPDATED_URL_FICHIER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cvs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CVRepository cVRepository;

    @Autowired
    private CVMapper cVMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CV cV;

    private CV insertedCV;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CV createEntity() {
        return new CV().urlFichier(DEFAULT_URL_FICHIER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CV createUpdatedEntity() {
        return new CV().urlFichier(UPDATED_URL_FICHIER);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CV.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        cV = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCV != null) {
            cVRepository.delete(insertedCV).block();
            insertedCV = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCV() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CV
        CVDTO cVDTO = cVMapper.toDto(cV);
        var returnedCVDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CVDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the CV in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCV = cVMapper.toEntity(returnedCVDTO);
        assertCVUpdatableFieldsEquals(returnedCV, getPersistedCV(returnedCV));

        insertedCV = returnedCV;
    }

    @Test
    void createCVWithExistingId() throws Exception {
        // Create the CV with an existing ID
        cV.setId(1L);
        CVDTO cVDTO = cVMapper.toDto(cV);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkUrlFichierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cV.setUrlFichier(null);

        // Create the CV, which fails.
        CVDTO cVDTO = cVMapper.toDto(cV);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCVSAsStream() {
        // Initialize the database
        cVRepository.save(cV).block();

        List<CV> cVList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CVDTO.class)
            .getResponseBody()
            .map(cVMapper::toEntity)
            .filter(cV::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cVList).isNotNull();
        assertThat(cVList).hasSize(1);
        CV testCV = cVList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertCVAllPropertiesEquals(cV, testCV);
        assertCVUpdatableFieldsEquals(cV, testCV);
    }

    @Test
    void getAllCVS() {
        // Initialize the database
        insertedCV = cVRepository.save(cV).block();

        // Get all the cVList
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
            .value(hasItem(cV.getId().intValue()))
            .jsonPath("$.[*].urlFichier")
            .value(hasItem(DEFAULT_URL_FICHIER));
    }

    @Test
    void getCV() {
        // Initialize the database
        insertedCV = cVRepository.save(cV).block();

        // Get the cV
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cV.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cV.getId().intValue()))
            .jsonPath("$.urlFichier")
            .value(is(DEFAULT_URL_FICHIER));
    }

    @Test
    void getNonExistingCV() {
        // Get the cV
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCV() throws Exception {
        // Initialize the database
        insertedCV = cVRepository.save(cV).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cV
        CV updatedCV = cVRepository.findById(cV.getId()).block();
        updatedCV.urlFichier(UPDATED_URL_FICHIER);
        CVDTO cVDTO = cVMapper.toDto(updatedCV);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cVDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCVToMatchAllProperties(updatedCV);
    }

    @Test
    void putNonExistingCV() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cV.setId(longCount.incrementAndGet());

        // Create the CV
        CVDTO cVDTO = cVMapper.toDto(cV);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cVDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCV() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cV.setId(longCount.incrementAndGet());

        // Create the CV
        CVDTO cVDTO = cVMapper.toDto(cV);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCV() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cV.setId(longCount.incrementAndGet());

        // Create the CV
        CVDTO cVDTO = cVMapper.toDto(cV);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCVWithPatch() throws Exception {
        // Initialize the database
        insertedCV = cVRepository.save(cV).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cV using partial update
        CV partialUpdatedCV = new CV();
        partialUpdatedCV.setId(cV.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCV.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCV))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CV in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCVUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCV, cV), getPersistedCV(cV));
    }

    @Test
    void fullUpdateCVWithPatch() throws Exception {
        // Initialize the database
        insertedCV = cVRepository.save(cV).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cV using partial update
        CV partialUpdatedCV = new CV();
        partialUpdatedCV.setId(cV.getId());

        partialUpdatedCV.urlFichier(UPDATED_URL_FICHIER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCV.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCV))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CV in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCVUpdatableFieldsEquals(partialUpdatedCV, getPersistedCV(partialUpdatedCV));
    }

    @Test
    void patchNonExistingCV() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cV.setId(longCount.incrementAndGet());

        // Create the CV
        CVDTO cVDTO = cVMapper.toDto(cV);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cVDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCV() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cV.setId(longCount.incrementAndGet());

        // Create the CV
        CVDTO cVDTO = cVMapper.toDto(cV);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCV() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cV.setId(longCount.incrementAndGet());

        // Create the CV
        CVDTO cVDTO = cVMapper.toDto(cV);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(cVDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CV in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCV() {
        // Initialize the database
        insertedCV = cVRepository.save(cV).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cV
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cV.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cVRepository.count().block();
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

    protected CV getPersistedCV(CV cV) {
        return cVRepository.findById(cV.getId()).block();
    }

    protected void assertPersistedCVToMatchAllProperties(CV expectedCV) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCVAllPropertiesEquals(expectedCV, getPersistedCV(expectedCV));
        assertCVUpdatableFieldsEquals(expectedCV, getPersistedCV(expectedCV));
    }

    protected void assertPersistedCVToMatchUpdatableProperties(CV expectedCV) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCVAllUpdatablePropertiesEquals(expectedCV, getPersistedCV(expectedCV));
        assertCVUpdatableFieldsEquals(expectedCV, getPersistedCV(expectedCV));
    }
}
