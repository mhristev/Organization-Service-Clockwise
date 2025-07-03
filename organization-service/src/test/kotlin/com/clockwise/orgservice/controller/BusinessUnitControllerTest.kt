package com.clockwise.orgservice.controller

import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.dto.BusinessUnitDto
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.toBusinessUnitDto
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.util.UUID

@WebFluxTest(BusinessUnitController::class)
class BusinessUnitControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var businessUnitService: BusinessUnitService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean
        fun businessUnitService(): BusinessUnitService = mockk()
    }

    @BeforeEach
    fun setup() {
        // Reset mocks if needed
    }

    @Test
    fun `get all business units should return all business units`(): Unit = runBlocking {
        // Arrange
        val businessUnits = listOf(
            BusinessUnit(
                id = UUID.randomUUID().toString(),
                name = "Business Unit 1",
                location = "Location 1",
                description = "Description 1",
                companyId = UUID.randomUUID().toString()
            ),
            BusinessUnit(
                id = UUID.randomUUID().toString(),
                name = "Business Unit 2", 
                location = "Location 2",
                description = "Description 2",
                companyId = UUID.randomUUID().toString()
            ),
        )
        val expectedDtos = businessUnits.map { it.toBusinessUnitDto() }

        coEvery { businessUnitService.getAllBusinessUnits() } returns flowOf(*businessUnits.toTypedArray())

        // Act & Assert
        val resultDtos = webTestClient.get()
            .uri("/v1/business-units")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(BusinessUnitDto::class.java)
            .returnResult().responseBody?.toList() ?: emptyList()

        assertEquals(expectedDtos, resultDtos)
    }

    @Test
    fun `create business unit should return created business unit`(): Unit = runBlocking {
        // Arrange
        val businessUnitId = UUID.randomUUID().toString()
        val businessUnit = BusinessUnit(
            id = businessUnitId,
            name = "Test Business Unit",
            location = "Test Location",
            description = "Test Description",
            companyId = UUID.randomUUID().toString()
        )
        val expectedDto = BusinessUnitDto(
            id = businessUnitId,
            name = "Test Business Unit",
            location = "Test Location",
            description = "Test Description",
            companyId = businessUnit.companyId
        )

        coEvery { businessUnitService.createBusinessUnit(any()) } returns businessUnit

        // Act & Assert
        webTestClient.post()
            .uri("/v1/business-units")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(businessUnit))
            .exchange()
            .expectStatus().isCreated
            .expectBody<BusinessUnitDto>()
            .isEqualTo(expectedDto)
    }

    @Test
    fun `get business unit by id should return business unit`(): Unit = runBlocking {
        // Arrange
        val businessUnitId = UUID.randomUUID().toString()
        val businessUnit = BusinessUnit(
            id = businessUnitId,
            name = "Test Business Unit",
            location = "Test Location", 
            description = "Test Description",
            companyId = UUID.randomUUID().toString()
        )
        val expectedDto = businessUnit.toBusinessUnitDto()

        coEvery { businessUnitService.getBusinessUnitById(businessUnitId) } returns businessUnit

        // Act & Assert
        webTestClient.get()
            .uri("/v1/business-units/$businessUnitId")
            .exchange()
            .expectStatus().isOk
            .expectBody<BusinessUnitDto>()
            .isEqualTo(expectedDto)
    }

    @Test
    fun `get business unit by id should return not found when business unit does not exist`(): Unit = runBlocking {
        // Arrange
        val businessUnitId = UUID.randomUUID().toString()

        coEvery { businessUnitService.getBusinessUnitById(businessUnitId) } returns null

        // Act & Assert
        webTestClient.get()
            .uri("/v1/business-units/$businessUnitId")
            .exchange()
            .expectStatus().isNotFound
    }
} 