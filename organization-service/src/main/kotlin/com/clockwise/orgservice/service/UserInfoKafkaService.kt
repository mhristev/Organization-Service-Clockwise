package com.clockwise.orgservice.service

import com.clockwise.orgservice.UserInfoRequest
import com.clockwise.orgservice.UserInfoResponse
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Service
class UserInfoKafkaService(
    private val stringKafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    
    @Value("\${kafka.topic.user-info-requests}")
    private lateinit var userInfoRequestsTopic: String
    
    // Store pending requests to match responses
    private val pendingRequests = ConcurrentHashMap<String, CompletableFuture<UserInfoResponse>>()
    
    fun requestUserInfo(userId: String): CompletableFuture<UserInfoResponse> {
        val request = UserInfoRequest(userId = userId)
        val future = CompletableFuture<UserInfoResponse>()
        
        logger.info { "Requesting user info for userId: $userId, requestId: ${request.requestId}" }
        
        // Store the future to complete it when response arrives
        pendingRequests[request.requestId] = future
        
        try {
            // Send the request to User Service as JSON string
            val message = objectMapper.writeValueAsString(request)
            logger.info { "Sending user info request message: $message" }
            stringKafkaTemplate.send(userInfoRequestsTopic, userId, message)
        } catch (e: Exception) {
            logger.error(e) { "Failed to send user info request for userId: $userId" }
            pendingRequests.remove(request.requestId)
            future.completeExceptionally(e)
            return future
        }
        
        // Set a timeout to avoid hanging indefinitely
        future.orTimeout(30, TimeUnit.SECONDS)
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    logger.warn { "User info request timed out or failed for userId: $userId, requestId: ${request.requestId}" }
                    pendingRequests.remove(request.requestId)
                }
            }
        
        return future
    }
    
    fun handleUserInfoResponse(response: UserInfoResponse) {
        logger.info { "Received user info response for userId: ${response.userId}, requestId: ${response.requestId}" }
        
        pendingRequests[response.requestId]?.let { future ->
            future.complete(response)
            pendingRequests.remove(response.requestId)
            logger.debug { "Completed user info request for userId: ${response.userId}, requestId: ${response.requestId}" }
        } ?: run {
            logger.warn { "Received response for unknown requestId: ${response.requestId}" }
        }
    }
    
    fun getPendingRequestsCount(): Int = pendingRequests.size
}