package com.clockwise.orgservice.service


import com.clockwise.orgservice.config.AbstractIntegrationTest
import com.clockwise.orgservice.config.Result
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
        val result = companyService.createCompany(Company(name = name, description = description))
        assertTrue(result is Result.Success, "Company creation should succeed")
        return (result as Result.Success).data!!
    }

    /** Helper function to assert `Result.Success` and extract data */
    private fun <T> assertSuccess(result: Result<T, *>, message: String = "Expected success"): T {
        assertTrue(result is Result.Success, message)
        return (result as Result.Success).data!!
    }

    @Test
    fun `should create company successfully`() = runBlocking {
        val newCompany = Company(name = "Test Company", description = "Test Description")

        val result = companyService.createCompany(newCompany)

        val savedCompany = assertSuccess(result)
        assertNotNull(savedCompany?.id)
        assertEquals(newCompany.name, savedCompany?.name)
        assertEquals(newCompany.description, savedCompany?.description)
    }

    @Test
    fun `should get company by id`() = runBlocking {
        val result = companyService.getCompanyById(testCompany.id!!)
        val retrievedCompany = assertSuccess(result)

        assertEquals(testCompany.id, retrievedCompany?.id)
        assertEquals(testCompany.name, retrievedCompany?.name)
        assertEquals(testCompany.description, retrievedCompany?.description)
    }

    @Test
    fun `should return null when getting non-existent company`() = runBlocking {
        val result = companyService.getCompanyById("non-existent-id")
        assertTrue(result is Result.Success)
        assertNull(result.data)
    }

    @Test
    fun `should update company`() = runBlocking {
        val updatedName = "Updated Company Name"
        val updatedDescription = "Updated Description"
        val updatedCompany = testCompany.copy(name = updatedName, description = updatedDescription)

        val result = companyService.updateCompany(testCompany.id!!, updatedCompany)

        val updatedData = assertSuccess(result)
        assertEquals(updatedName, updatedData?.name)
        assertEquals(updatedDescription, updatedData?.description)

        // Verify with get
        val getResult = companyService.getCompanyById(testCompany.id!!)
        val retrievedCompany = assertSuccess(getResult)
        assertEquals(updatedName, retrievedCompany?.name)
        assertEquals(updatedDescription, retrievedCompany?.description)
    }

    @Test
    fun `should delete company`() = runBlocking {
        val deleteResult = companyService.deleteCompany(testCompany.id!!)
        assertTrue(deleteResult is Result.Success)

        val getResult = companyService.getCompanyById(testCompany.id!!)
        assertTrue(getResult is Result.Success)
        assertNull(getResult.data)
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