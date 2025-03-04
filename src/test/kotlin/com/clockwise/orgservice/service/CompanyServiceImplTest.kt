package com.clockwise.orgservice.service


import com.clockwise.orgservice.config.AbstractIntegrationTest
import com.clockwise.orgservice.domain.Company
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
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class CompanyServiceImplTest @Autowired constructor(
    private val companyService: CompanyService,
    private val flyway: Flyway
) : AbstractIntegrationTest() {

    private lateinit var testCompany: Company

    @BeforeEach
    fun setup() = runBlocking {
        flyway.clean()
        flyway.migrate()
        // Create a default company for most tests
        testCompany = createTestCompany("Default Company", "Default Description")
    }

    /** Helper function to create a company */
    private suspend fun createTestCompany(name: String, description: String): Company {
        return companyService.createCompany(Company(name = name, description = description))
    }

    @Test
    fun `should create company successfully`() = runBlocking {
        val newCompany = Company(name = "Test Company", description = "Test Description")

        val savedCompany = companyService.createCompany(newCompany)
        assertNotNull(savedCompany.id)
        assertEquals(newCompany.name, savedCompany.name)
        assertEquals(newCompany.description, savedCompany.description)
    }

    @Test
    fun `should get company by id`() = runBlocking {
        val retrievedCompany = companyService.getCompanyById(testCompany.id!!)
        assertNotNull(retrievedCompany)
        assertEquals(testCompany.id, retrievedCompany.id)
        assertEquals(testCompany.name, retrievedCompany.name)
        assertEquals(testCompany.description, retrievedCompany.description)
    }

    @Test
    fun `should return null when getting non-existent company`() = runBlocking {
        val result = companyService.getCompanyById("non-existent-id")
        assertNull(result)
    }

    @Test
    fun `should update company`() = runBlocking {
        val updatedName = "Updated Company Name"
        val updatedDescription = "Updated Description"
        val updatedCompany = testCompany.copy(name = updatedName, description = updatedDescription)

        val updatedData = companyService.updateCompany(testCompany.id!!, updatedCompany)
        assertNotNull(updatedData)
        assertEquals(updatedName, updatedData.name)
        assertEquals(updatedDescription, updatedData.description)

        // Verify with get
        val retrievedCompany = companyService.getCompanyById(testCompany.id!!)
        assertNotNull(retrievedCompany)
        assertEquals(updatedName, retrievedCompany.name)
        assertEquals(updatedDescription, retrievedCompany.description)
    }

    @Test
    fun `should delete company`() = runBlocking {
        companyService.deleteCompany(testCompany.id!!)

        val getResult = companyService.getCompanyById(testCompany.id!!)
        assertNull(getResult)
    }

//    @Test
//    fun `should handle invalid input when creating company with empty name`(): Unit = runBlocking {
//        val invalidCompany = Company(name = "", description = "Test Description")
//
//        val result = companyService.createCompany(invalidCompany)
//
//        assertTrue(result is Result.Error)
//        assertNotNull(result.error)
//    }
}