package com.clockwise.orgservice.controller


import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.domain.dto.BusinessUnitDto
import com.clockwise.orgservice.domain.dto.CompanyDto
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.service.CompanyService
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

@WebFluxTest(CompanyController::class)
class CompanyControllerIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var companyService: CompanyService

    @Autowired
    private lateinit var businessUnitService: BusinessUnitService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class TestConfig {
        @Bean
        fun companyService(): CompanyService = mockk()

        @Bean
        fun businessUnitService(): BusinessUnitService = mockk()
    }

    @BeforeEach
    fun setup() {
        // Reset mocks if needed
    }

    @Test
    fun `create company should return created company`(): Unit = runBlocking {
        // Arrange
        val companyId = UUID.randomUUID().toString()
        val company = Company(
            id = companyId,
            name = "Test Company",
            description = "A test company"
        )
        val expectedDto = CompanyDto(
            id = companyId,
            name = "Test Company",
            description = "A test company"
        )

        coEvery { companyService.createCompany(any()) } returns company

        // Act & Assert
        webTestClient.post()
            .uri("/v1/companies")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(company))
            .exchange()
            .expectStatus().isCreated
            .expectBody<CompanyDto>()
            .isEqualTo(expectedDto)
    }

    @Test
    fun `get company by existing id should return company`(): Unit = runBlocking {
        // Arrange
        val companyId = UUID.randomUUID().toString()
        val company = Company(
            id = companyId,
            name = "Existing Company",
            description = "An existing company"
        )
        val expectedDto = CompanyDto(
            id = companyId,
            name = "Existing Company",
            description = "An existing company"
        )

        coEvery { companyService.getCompanyById(companyId) } returns company

        // Act & Assert
        webTestClient.get()
            .uri("/v1/companies/$companyId")
            .exchange()
            .expectStatus().isOk
            .expectBody<CompanyDto>()
            .isEqualTo(expectedDto)
    }

    @Test
    fun `get company by non-existing id should return not found`(): Unit = runBlocking {
        // Arrange
        val companyId = UUID.randomUUID().toString()

        coEvery { companyService.getCompanyById(companyId) } returns null

        // Act & Assert
        webTestClient.get()
            .uri("/v1/companies/$companyId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `update company should return updated company`(): Unit = runBlocking {
        // Arrange
        val companyId = UUID.randomUUID().toString()
        val company = Company(
            id = companyId,
            name = "Updated Company",
            description = "An updated company"
        )
        val expectedDto = CompanyDto(
            id = companyId,
            name = "Updated Company",
            description = "An updated company"
        )

        coEvery { companyService.updateCompany(companyId, any()) } returns company

        // Act & Assert
        webTestClient.put()
            .uri("/v1/companies/$companyId")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(company))
            .exchange()
            .expectStatus().isOk
            .expectBody<CompanyDto>()
            .isEqualTo(expectedDto)
    }

    @Test
    fun `delete company should return no content`(): Unit = runBlocking {
        // Arrange
        val companyId = UUID.randomUUID().toString()

        coEvery { companyService.deleteCompany(companyId) } returns Unit

        // Act & Assert
        webTestClient.delete()
            .uri("/v1/companies/$companyId")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `get company business units should return business units`(): Unit = runBlocking {
        // Arrange
        val companyId = UUID.randomUUID().toString()
        val businessUnits = listOf(
            BusinessUnit(
                id = UUID.randomUUID().toString(),
                name = "Business Unit 1",
                location = "Location",
                description = "description",
                companyId = companyId
            ),
            BusinessUnit(
                id = UUID.randomUUID().toString(),
                name = "Business Unit 2",
                location = "Location 2",
                description = "description 2",
                companyId = companyId
            ),
        )
        val expectedDtos = businessUnits.map { it.toBusinessUnitDto() }

        coEvery { businessUnitService.getBusinessUnitsByCompanyId(companyId) } returns flowOf(*businessUnits.toTypedArray())

        // Act & Assert
        val resultDtos = webTestClient.get()
            .uri("/v1/companies/$companyId/business-units")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(BusinessUnitDto::class.java)
            .returnResult().responseBody?.toList() ?: emptyList()

        assertEquals(expectedDtos, resultDtos)
    }
}