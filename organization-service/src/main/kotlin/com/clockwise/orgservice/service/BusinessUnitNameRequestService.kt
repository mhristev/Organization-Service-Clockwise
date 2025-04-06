package com.clockwise.orgservice.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

data class BusinessUnitNameRequest(
    val userId: String,
    val businessUnitId: String
)

data class BusinessUnitNameResponse(
    val userId: String,
    val businessUnitId: String,
    val businessUnitName: String
)

@Service
class BusinessUnitNameRequestService(
    private val stringKafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val businessUnitService: BusinessUnitService
) {
    private val logger = LoggerFactory.getLogger(BusinessUnitNameRequestService::class.java)

    @Value("\${kafka.topic.business-unit-name-requests}")
    private lateinit var businessUnitNameRequestsTopic: String

    @Value("\${kafka.topic.business-unit-name-responses}")
    private lateinit var businessUnitNameResponsesTopic: String

    suspend fun handleBusinessUnitNameRequest(request: BusinessUnitNameRequest) {
        try {
            logger.info("Received business unit name request: {}", request)
            
            // Get the business unit name from the service
            val businessUnit = businessUnitService.getBusinessUnitById(request.businessUnitId)
            
            // Create and send the response
            val response = businessUnit?.let {
                BusinessUnitNameResponse(
                    userId = request.userId,
                    businessUnitId = request.businessUnitId,
                    businessUnitName = it.name
                )
            }
            
            val message = objectMapper.writeValueAsString(response)
            logger.info("Sending business unit name response: {}", message)
            
            stringKafkaTemplate.send(businessUnitNameResponsesTopic, message)

        } catch (e: Exception) {
            logger.error("Error processing business unit name request", e)
            throw e
        }
    }
} 