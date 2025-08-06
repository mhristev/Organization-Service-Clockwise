package com.clockwise.orgservice.listener

import com.clockwise.orgservice.UserInfoResponse
import com.clockwise.orgservice.service.UserInfoKafkaService
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class UserInfoResponseListener(
    private val userInfoKafkaService: UserInfoKafkaService,
    private val objectMapper: ObjectMapper
) {
    
    @KafkaListener(
        topics = ["\${kafka.topic.user-info-responses}"], 
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "stringKafkaListenerContainerFactory"
    )
    fun handleUserInfoResponse(message: String, ack: Acknowledgment) {
        try {
            logger.debug { "Received user info response message: $message" }
            
            val response = objectMapper.readValue(message, UserInfoResponse::class.java)
            
            logger.info { 
                "Processing user info response for userId: ${response.userId}, " +
                "requestId: ${response.requestId}, found: ${response.found}" 
            }
            
            // Forward the response to the service to complete the pending request
            userInfoKafkaService.handleUserInfoResponse(response)
            
            ack.acknowledge()
            logger.info { "Successfully processed user info response for requestId: ${response.requestId}" }
            
        } catch (e: Exception) {
            logger.error(e) { "Error processing user info response message: $message" }
            throw e
        }
    }
}