package com.clockwise.orgservice.service

import com.clockwise.orgservice.BusinessUnitEvent
import com.clockwise.orgservice.EventType
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service


@Service
class KafkaService(
    private val kafkaTemplate: KafkaTemplate<String, BusinessUnitEvent>
) {
    @Value("\${kafka.topic.business-unit-events}")
    private lateinit var businessUnitEventsTopic: String

    fun sendBusinessUnitCreatedMessage(id: String, name: String) {
        val event = BusinessUnitEvent(id, name, EventType.CREATED)
        println("-------------------------------------------------------> Sending event: $event")
        kafkaTemplate.send(businessUnitEventsTopic, id, event)
    }

    fun sendBusinessUnitUpdatedMessage(id: String, name: String) {
        val event = BusinessUnitEvent(id, name, EventType.UPDATED)
        println("-------------------------------------------------------> Sending event: $event")
        kafkaTemplate.send(businessUnitEventsTopic, id, event)
    }

    fun sendBusinessUnitDeletedMessage(id: String, name: String) {
        val event = BusinessUnitEvent(id, name, EventType.DELETED)
        println("-------------------------------------------------------> Sending event: $event")
        kafkaTemplate.send(businessUnitEventsTopic, id, event)
    }
} 