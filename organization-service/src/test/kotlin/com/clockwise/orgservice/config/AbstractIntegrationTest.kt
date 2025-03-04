package com.clockwise.orgservice.config
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class AbstractIntegrationTest {

    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:14").apply {
            withDatabaseName("test-db")
            withUsername("test")
            withPassword("test")
        }

        @JvmStatic
        @DynamicPropertySource
        fun overrideProperties(registry: DynamicPropertyRegistry) {
            val host = postgresContainer.host
            val port = postgresContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
            val database = postgresContainer.databaseName
            val username = postgresContainer.username
            val password = postgresContainer.password

            // Override R2DBC properties
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://$host:$port/$database"
            }
            registry.add("spring.r2dbc.username") { username }
            registry.add("spring.r2dbc.password") { password }

            // Override Flyway properties (if using Flyway with JDBC)
            registry.add("spring.flyway.url") {
                "jdbc:postgresql://$host:$port/$database"
            }
            registry.add("spring.flyway.user") { username }
            registry.add("spring.flyway.password") { password }
        }
    }
}
