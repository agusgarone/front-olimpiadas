package com.olimpiadas.www.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.olimpiadas.www.IntegrationTest;
import com.olimpiadas.www.domain.Region;
import com.olimpiadas.www.repository.RegionRepository;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RegionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RegionResourceIT {

    private static final String DEFAULT_REGION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_REGION_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/regions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Region region;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createEntity() {
        Region region = new Region().regionName(DEFAULT_REGION_NAME);
        return region;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Region createUpdatedEntity() {
        Region region = new Region().regionName(UPDATED_REGION_NAME);
        return region;
    }

    @BeforeEach
    public void initTest() {
        regionRepository.deleteAll().block();
        region = createEntity();
    }

    @Test
    void createRegion() throws Exception {
        int databaseSizeBeforeCreate = regionRepository.findAll().collectList().block().size();
        // Create the Region
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeCreate + 1);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getRegionName()).isEqualTo(DEFAULT_REGION_NAME);
    }

    @Test
    void createRegionWithExistingId() throws Exception {
        // Create the Region with an existing ID
        region.setId("existing_id");

        int databaseSizeBeforeCreate = regionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRegionsAsStream() {
        // Initialize the database
        regionRepository.save(region).block();

        List<Region> regionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Region.class)
            .getResponseBody()
            .filter(region::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(regionList).isNotNull();
        assertThat(regionList).hasSize(1);
        Region testRegion = regionList.get(0);
        assertThat(testRegion.getRegionName()).isEqualTo(DEFAULT_REGION_NAME);
    }

    @Test
    void getAllRegions() {
        // Initialize the database
        regionRepository.save(region).block();

        // Get all the regionList
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
            .value(hasItem(region.getId()))
            .jsonPath("$.[*].regionName")
            .value(hasItem(DEFAULT_REGION_NAME));
    }

    @Test
    void getRegion() {
        // Initialize the database
        regionRepository.save(region).block();

        // Get the region
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, region.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(region.getId()))
            .jsonPath("$.regionName")
            .value(is(DEFAULT_REGION_NAME));
    }

    @Test
    void getNonExistingRegion() {
        // Get the region
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRegion() throws Exception {
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();

        // Update the region
        Region updatedRegion = regionRepository.findById(region.getId()).block();
        updatedRegion.regionName(UPDATED_REGION_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRegion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getRegionName()).isEqualTo(UPDATED_REGION_NAME);
    }

    @Test
    void putNonExistingRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, region.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        partialUpdatedRegion.regionName(UPDATED_REGION_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getRegionName()).isEqualTo(UPDATED_REGION_NAME);
    }

    @Test
    void fullUpdateRegionWithPatch() throws Exception {
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();

        // Update the region using partial update
        Region partialUpdatedRegion = new Region();
        partialUpdatedRegion.setId(region.getId());

        partialUpdatedRegion.regionName(UPDATED_REGION_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRegion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRegion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
        Region testRegion = regionList.get(regionList.size() - 1);
        assertThat(testRegion.getRegionName()).isEqualTo(UPDATED_REGION_NAME);
    }

    @Test
    void patchNonExistingRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, region.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRegion() throws Exception {
        int databaseSizeBeforeUpdate = regionRepository.findAll().collectList().block().size();
        region.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(region))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Region in the database
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRegion() {
        // Initialize the database
        regionRepository.save(region).block();

        int databaseSizeBeforeDelete = regionRepository.findAll().collectList().block().size();

        // Delete the region
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, region.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Region> regionList = regionRepository.findAll().collectList().block();
        assertThat(regionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}