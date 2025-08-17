package com.clockwise.orgservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().apply {
            registerKotlinModule()
            
            // Disable features that might strip formatting
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            
            // Ensure proper handling of strings with formatting
            // This preserves whitespace, newlines, and other formatting in text fields
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
