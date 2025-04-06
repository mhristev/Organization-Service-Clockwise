package com.clockwise.orgservice.listener

import com.clockwise.orgservice.service.BusinessUnitNameRequest
import com.clockwise.orgservice.service.BusinessUnitNameRequestService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class BusinessUnitNameRequestListener(
    private val businessUnitNameRequestService: BusinessUnitNameRequestService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(BusinessUnitNameRequestListener::class.java)

    @KafkaListener(
        topics = ["\${kafka.topic.business-unit-name-requests}"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "stringKafkaListenerContainerFactory"
    )
    suspend fun handleBusinessUnitNameRequest(message: String, ack: Acknowledgment) {
        try {
            logger.info("Received business unit name request message: {}", message)
            val request = objectMapper.readValue(message, BusinessUnitNameRequest::class.java)
            businessUnitNameRequestService.handleBusinessUnitNameRequest(request)
            ack.acknowledge()
            logger.info("Successfully processed business unit name request")
        } catch (e: Exception) {
            logger.error("Error processing business unit name request message", e)
            throw e
        }
    }
} 