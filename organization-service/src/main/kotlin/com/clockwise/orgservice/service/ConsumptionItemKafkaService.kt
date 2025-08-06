package com.clockwise.orgservice.service

import com.clockwise.orgservice.event.ConsumptionItemEvent
import com.clockwise.orgservice.event.ConsumptionItemEventData
import com.clockwise.orgservice.event.ConsumptionItemEventType
import com.clockwise.orgservice.domain.ConsumptionItem
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class ConsumptionItemKafkaService(
    private val stringKafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    
    @Value("\${kafka.topic.consumption-item-events}")
    private lateinit var consumptionItemEventsTopic: String
    
    fun sendConsumptionItemCreatedEvent(consumptionItem: ConsumptionItem) {
        sendConsumptionItemEvent(consumptionItem, ConsumptionItemEventType.CREATED)
    }
    
    fun sendConsumptionItemUpdatedEvent(consumptionItem: ConsumptionItem) {
        sendConsumptionItemEvent(consumptionItem, ConsumptionItemEventType.UPDATED)
    }
    
    fun sendConsumptionItemDeletedEvent(consumptionItem: ConsumptionItem) {
        sendConsumptionItemEvent(consumptionItem, ConsumptionItemEventType.DELETED)
    }
    
    private fun sendConsumptionItemEvent(consumptionItem: ConsumptionItem, eventType: ConsumptionItemEventType) {
        try {
            val eventData = ConsumptionItemEventData(
                id = consumptionItem.id ?: "",
                name = consumptionItem.name,
                price = consumptionItem.price,
                type = consumptionItem.type,
                businessUnitId = consumptionItem.businessUnitId,
                createdAt = consumptionItem.createdAt,
                updatedAt = consumptionItem.updatedAt
            )
            
            val event = ConsumptionItemEvent(
                eventType = eventType,
                consumptionItem = eventData,
                businessUnitId = consumptionItem.businessUnitId
            )
            
            val message = objectMapper.writeValueAsString(event)
            
            logger.info { "Sending consumption item $eventType event for item: ${consumptionItem.id} in business unit: ${consumptionItem.businessUnitId}" }
            
            stringKafkaTemplate.send(
                consumptionItemEventsTopic, 
                consumptionItem.id ?: "",
                message
            )
            
            logger.debug { "Successfully sent consumption item event: $message" }
            
        } catch (e: Exception) {
            logger.error(e) { "Failed to send consumption item $eventType event for item: ${consumptionItem.id}" }
        }
    }
}
