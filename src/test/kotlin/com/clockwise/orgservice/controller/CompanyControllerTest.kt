package com.clockwise.orgservice.controller


import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.domain.dto.CompanyDto
import com.clockwise.orgservice.service.CompanyService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
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
 private lateinit var objectMapper: ObjectMapper

 @TestConfiguration
 class TestConfig {
  @Bean
  fun companyService(): CompanyService = mockk()
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
   name = "Test Companya",
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
}

// Additional helper extension function if needed
//fun <T> WebTestClient.BodyContentSpec.isEqualTo(expected: T) {
// this.consumeWith { response ->
//  val body = response.responseBody
//  assert(body != null) { "Response body should not be null" }
//  assert(jacksonObjectMapper().writeValueAsString(expected) == String(body!!)) {
//   "Expected ${jacksonObjectMapper().writeValueAsString(expected)}, but got ${String(body)}"
//  }
// }
//}
//import com.clockwise.orgservice.domain.Company
//import com.clockwise.orgservice.domain.dto.CompanyDto
//import com.clockwise.orgservice.service.CompanyService
//import com.clockwise.orgservice.toCompanyDto
//import org.junit.jupiter.api.Assertions.*
//import com.fasterxml.jackson.databind.ObjectMapper
//import kotlinx.coroutines.async
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.runBlocking
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.mockito.Mockito.verify
//import org.mockito.junit.jupiter.MockitoExtension
//import org.springframework.http.HttpStatus
//import org.springframework.http.MediaType
//import org.springframework.test.web.servlet.MockMvc
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
//import org.springframework.test.web.servlet.setup.MockMvcBuilders
//
//@ExtendWith(MockitoExtension::class)
//class CompanyControllerTest {
//
// @Mock
// private lateinit var companyService: CompanyService
//
// @InjectMocks
// private lateinit var companyController: CompanyController
//
// private lateinit var mockMvc: MockMvc
//
// private val objectMapper = ObjectMapper()
//
// @BeforeEach
// fun setup() {
//  mockMvc = MockMvcBuilders.standaloneSetup(companyController).build()
// }
//
// @Test
// fun `createCompany should return CREATED and CompanyDto`(): Unit = runBlocking {
//  val company = Company("1", "Test Company", description = "Test Description")
//  val companyDto = company.toCompanyDto()
//
//  `when`(companyService.createCompany(company)).thenReturn(company)
//
//  mockMvc.perform(
//   post("/v1/companies")
//    .contentType(MediaType.APPLICATION_JSON)
//    .content(objectMapper.writeValueAsString(company))
//  )
//   .andExpect(status().isCreated)
//   .andExpect {
//    val response = objectMapper.readValue(it.response.contentAsString, CompanyDto::class.java)
//    assertEquals(companyDto, response)
//   }
// }
//
// @Test
// fun `getCompanyById should return OK and CompanyDto when company exists`(): Unit = runBlocking {
//  val company = Company("1", "Test Company", description = "Test Description")
//  val companyDto = company.toCompanyDto()
//
//  `when`(companyService.getCompanyById("1")).thenReturn(company)
//
//  mockMvc.perform(get("/v1/companies/1"))
//   .andExpect(status().isOk)
//   .andExpect {
//    val response = objectMapper.readValue(it.response.contentAsString, CompanyDto::class.java)
//    assertEquals(companyDto, response)
//   }
// }
//
// @Test
// fun `getCompanyById should return NOT_FOUND when company does not exist`() = runBlocking {
//  `when`(companyService.getCompanyById("1")).thenReturn(null)
//
//  mockMvc.perform(get("/v1/companies/1"))
//   .andExpect(status().isNotFound)
// }
//
// @Test
// fun `updateCompany should return OK and CompanyDto`() = runBlocking {
//  val company = Company("1", "Updated Company")
//  val companyDto = company.toCompanyDto()
//
//  `when`(companyService.updateCompany("1", company)).thenReturn(company)
//
//  mockMvc.perform(
//   put("/v1/companies/1")
//    .contentType(MediaType.APPLICATION_JSON)
//    .content(objectMapper.writeValueAsString(company))
//  )
//   .andExpect(status().isOk)
//   .andExpect {
//    val response = objectMapper.readValue(it.response.contentAsString, CompanyDto::class.java)
//    assertEquals(companyDto, response)
//   }
// }
//
// @Test
// fun `deleteCompany should return NO_CONTENT`() = runBlocking {
//  mockMvc.perform(delete("/v1/companies/1"))
//   .andExpect(status().isNoContent)
//
//  verify(companyService).deleteCompany("1")
// }
//}
