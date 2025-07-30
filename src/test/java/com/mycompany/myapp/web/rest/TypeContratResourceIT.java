package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TypeContratAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.TypeContratRepository;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import com.mycompany.myapp.service.mapper.TypeContratMapper;
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
 * Integration tests for the {@link TypeContratResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TypeContratResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-contrats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TypeContratRepository typeContratRepository;

    @Autowired
    private TypeContratMapper typeContratMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TypeContrat typeContrat;

    private TypeContrat insertedTypeContrat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeContrat createEntity() {
        return new TypeContrat().nom(DEFAULT_NOM);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeContrat createUpdatedEntity() {
        return new TypeContrat().nom(UPDATED_NOM);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TypeContrat.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        typeContrat = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTypeContrat != null) {
            typeContratRepository.delete(insertedTypeContrat).block();
            insertedTypeContrat = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTypeContrat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);
        var returnedTypeContratDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TypeContratDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the TypeContrat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTypeContrat = typeContratMapper.toEntity(returnedTypeContratDTO);
        assertTypeContratUpdatableFieldsEquals(returnedTypeContrat, getPersistedTypeContrat(returnedTypeContrat));

        insertedTypeContrat = returnedTypeContrat;
    }

    @Test
    void createTypeContratWithExistingId() throws Exception {
        // Create the TypeContrat with an existing ID
        typeContrat.setId(1L);
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        typeContrat.setNom(null);

        // Create the TypeContrat, which fails.
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTypeContratsAsStream() {
        // Initialize the database
        typeContratRepository.save(typeContrat).block();

        List<TypeContrat> typeContratList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TypeContratDTO.class)
            .getResponseBody()
            .map(typeContratMapper::toEntity)
            .filter(typeContrat::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(typeContratList).isNotNull();
        assertThat(typeContratList).hasSize(1);
        TypeContrat testTypeContrat = typeContratList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertTypeContratAllPropertiesEquals(typeContrat, testTypeContrat);
        assertTypeContratUpdatableFieldsEquals(typeContrat, testTypeContrat);
    }

    @Test
    void getAllTypeContrats() {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.save(typeContrat).block();

        // Get all the typeContratList
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
            .value(hasItem(typeContrat.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM));
    }

    @Test
    void getTypeContrat() {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.save(typeContrat).block();

        // Get the typeContrat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, typeContrat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(typeContrat.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM));
    }

    @Test
    void getNonExistingTypeContrat() {
        // Get the typeContrat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTypeContrat() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.save(typeContrat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeContrat
        TypeContrat updatedTypeContrat = typeContratRepository.findById(typeContrat.getId()).block();
        updatedTypeContrat.nom(UPDATED_NOM);
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(updatedTypeContrat);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typeContratDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTypeContratToMatchAllProperties(updatedTypeContrat);
    }

    @Test
    void putNonExistingTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, typeContratDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTypeContratWithPatch() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.save(typeContrat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeContrat using partial update
        TypeContrat partialUpdatedTypeContrat = new TypeContrat();
        partialUpdatedTypeContrat.setId(typeContrat.getId());

        partialUpdatedTypeContrat.nom(UPDATED_NOM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypeContrat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTypeContrat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeContrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTypeContratUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTypeContrat, typeContrat),
            getPersistedTypeContrat(typeContrat)
        );
    }

    @Test
    void fullUpdateTypeContratWithPatch() throws Exception {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.save(typeContrat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeContrat using partial update
        TypeContrat partialUpdatedTypeContrat = new TypeContrat();
        partialUpdatedTypeContrat.setId(typeContrat.getId());

        partialUpdatedTypeContrat.nom(UPDATED_NOM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTypeContrat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTypeContrat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TypeContrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTypeContratUpdatableFieldsEquals(partialUpdatedTypeContrat, getPersistedTypeContrat(partialUpdatedTypeContrat));
    }

    @Test
    void patchNonExistingTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, typeContratDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTypeContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeContrat.setId(longCount.incrementAndGet());

        // Create the TypeContrat
        TypeContratDTO typeContratDTO = typeContratMapper.toDto(typeContrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(typeContratDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TypeContrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTypeContrat() {
        // Initialize the database
        insertedTypeContrat = typeContratRepository.save(typeContrat).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the typeContrat
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, typeContrat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return typeContratRepository.count().block();
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

    protected TypeContrat getPersistedTypeContrat(TypeContrat typeContrat) {
        return typeContratRepository.findById(typeContrat.getId()).block();
    }

    protected void assertPersistedTypeContratToMatchAllProperties(TypeContrat expectedTypeContrat) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTypeContratAllPropertiesEquals(expectedTypeContrat, getPersistedTypeContrat(expectedTypeContrat));
        assertTypeContratUpdatableFieldsEquals(expectedTypeContrat, getPersistedTypeContrat(expectedTypeContrat));
    }

    protected void assertPersistedTypeContratToMatchUpdatableProperties(TypeContrat expectedTypeContrat) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTypeContratAllUpdatablePropertiesEquals(expectedTypeContrat, getPersistedTypeContrat(expectedTypeContrat));
        assertTypeContratUpdatableFieldsEquals(expectedTypeContrat, getPersistedTypeContrat(expectedTypeContrat));
    }
}
