package com.clockwise.orgservice.config

import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired

@Configuration
class WebConfig : WebFluxConfigurer {
    
    @Autowired
    private lateinit var corsConfigurationSource: CorsConfigurationSource

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        return CorsWebFilter(corsConfigurationSource)
    }
}
