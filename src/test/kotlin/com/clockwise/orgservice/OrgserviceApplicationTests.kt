package com.clockwise.orgservice

import com.clockwise.orgservice.config.AbstractIntegrationTest
import com.clockwise.orgservice.domain.Company
import com.clockwise.orgservice.repositories.CompanyRepository
import com.example.demo.TestcontainersConfiguration
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
//@Import(AbstractIntegrationTest::class)
class OrgserviceApplicationTests(@Autowired val companyRepository: CompanyRepository) : AbstractIntegrationTest() {

	@Test
	fun contextLoads() {
		runBlocking {
			companyRepository.save(Company(name = "Test Company", description = "Test Description"))
			val c = companyRepository.findAll()
			Assertions.assertNotNull(c)

		}
	}



}
