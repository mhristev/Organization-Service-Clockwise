package com.clockwise.orgservice.service.impl

import com.clockwise.orgservice.config.AbstractIntegrationTest
import com.clockwise.orgservice.domain.BusinessUnit
import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.service.BusinessUnitService
import com.clockwise.orgservice.service.CompanyService
import kotlinx.coroutines.runBlocking
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
class BusinessUnitServiceImplTest @Autowired constructor(
 private val businessUnitService: BusinessUnitService,
 private val companyService: CompanyService,
 private val flyway: Flyway
) : AbstractIntegrationTest() {
    private lateinit var testBusinessUnit: BusinessUnit

    @BeforeEach
    fun setup() = runBlocking {
        flyway.clean()
        flyway.migrate()
        // Create a default business unit for most tests
        testBusinessUnit = createTestBusinessUnit("Default Business Unit", "Default Location", "Default Description")
    }

/** Helper function to create a business unit */
private suspend fun createTestBusinessUnit(name: String, location: String, description: String): BusinessUnit {
 val companyId = insertCompany()

    return businessUnitService.createBusinessUnit(
        BusinessUnit(
            name = name,
            location = location,
            description = description,
            companyId =companyId
        )
    )
}
 suspend fun insertCompany(): String {
  // Assuming you have a method to insert a company and return its ID
  return companyService.createCompany(Company(name = "Test Name", description = "Test Description")).id!!
 }

@Test
fun `should create business unit successfully`() = runBlocking {
    val companyId = insertCompany()
    val newBusinessUnit = BusinessUnit(
        name = "Test Business Unit",
        location = "Test Location",
        description = "Test Description",
        companyId = companyId
    )
    val savedBusinessUnit = businessUnitService.createBusinessUnit(newBusinessUnit)
    assertNotNull(savedBusinessUnit.id)
    assertEquals(newBusinessUnit.name, savedBusinessUnit.name)
    assertEquals(newBusinessUnit.location, savedBusinessUnit.location)
    assertEquals(newBusinessUnit.description, savedBusinessUnit.description)
}

@Test
fun `should get business unit by id`() = runBlocking {
    val retrievedBusinessUnit = businessUnitService.getBusinessUnitById(testBusinessUnit.id!!)
    assertNotNull(retrievedBusinessUnit)
    assertEquals(testBusinessUnit.id, retrievedBusinessUnit.id)
    assertEquals(testBusinessUnit.name, retrievedBusinessUnit.name)
    assertEquals(testBusinessUnit.location, retrievedBusinessUnit.location)
    assertEquals(testBusinessUnit.description, retrievedBusinessUnit.description)
}

@Test
fun `should return null when getting non-existent business unit`() = runBlocking {
    val result = businessUnitService.getBusinessUnitById("non-existent-id")
    assertNull(result)
}

@Test
fun `should update business unit`() = runBlocking {
    val updatedName = "Updated Business Unit Name"
    val updatedLocation = "Updated Location"
    val updatedDescription = "Updated Description"
    val updatedBusinessUnit =
        testBusinessUnit.copy(name = updatedName, location = updatedLocation, description = updatedDescription)

    val updatedData = businessUnitService.updateBusinessUnit(updatedBusinessUnit)
    assertNotNull(updatedData)
    assertEquals(updatedName, updatedData.name)
    assertEquals(updatedLocation, updatedData.location)
    assertEquals(updatedDescription, updatedData.description)

    // Verify with get
    val retrievedBusinessUnit = businessUnitService.getBusinessUnitById(testBusinessUnit.id!!)
    assertNotNull(retrievedBusinessUnit)
    assertEquals(updatedName, retrievedBusinessUnit.name)
    assertEquals(updatedLocation, retrievedBusinessUnit.location)
    assertEquals(updatedDescription, retrievedBusinessUnit.description)
}

@Test
fun `should delete business unit`() = runBlocking {
    businessUnitService.deleteBusinessUnit(testBusinessUnit.id!!)

    val getResult = businessUnitService.getBusinessUnitById(testBusinessUnit.id!!)
    assertNull(getResult)
}
}