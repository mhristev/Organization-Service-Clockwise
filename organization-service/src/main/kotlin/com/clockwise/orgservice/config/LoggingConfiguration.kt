package com.clockwise.orgservice.config

import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.net.InetAddress

private val logger = KotlinLogging.logger {}

@Configuration
class LoggingConfiguration(private val env: Environment) : ApplicationListener<ApplicationStartedEvent> {

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val protocol = if (env.getProperty("server.ssl.key-store") != null) "https" else "http"
        val port = env.getProperty("server.port") ?: "8080"
        val contextPath = env.getProperty("server.servlet.context-path") ?: "/"
        val hostAddress = InetAddress.getLocalHost().hostAddress
        val applicationName = env.getProperty("spring.application.name") ?: "organization-service"
        
        logger.info {
            """
            |------------------------------------------------------------
            | Application '$applicationName' is running!
            | Local: $protocol://localhost:$port$contextPath
            | External: $protocol://$hostAddress:$port$contextPath
            | Profile(s): ${env.activeProfiles.joinToString(", ")}
            |------------------------------------------------------------
            """.trimMargin()
        }
    }
} 