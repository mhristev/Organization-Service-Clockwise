package com.clockwise.orgservice.util

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class LoggingInterceptor : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        val request = exchange.request
        val path = request.path.value()
        val method = request.method

        logger.info { "Request [$requestId] started: $method $path" }
        
        return chain.filter(exchange)
            .doOnSuccess { 
                val duration = System.currentTimeMillis() - startTime
                val status = exchange.response.statusCode ?: HttpStatus.OK
                logger.info { "Request [$requestId] completed: $method $path - $status in ${duration}ms" }
            }
            .doOnError { throwable ->
                val duration = System.currentTimeMillis() - startTime
                logger.error { "Request [$requestId] failed: $method $path - Error: ${throwable.message} in ${duration}ms" }
            }
    }
} 