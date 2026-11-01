package org.korolev.autopartsstoreanalyticsservice.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.korolev.autopartsstoreanalyticsservice.event.EventClickHouseRepository;
import org.korolev.autopartsstoreanalyticsservice.event.EventMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final JsonMapper jsonMapper;
    private final EventClickHouseRepository eventClickHouseRepository;

    @KafkaListener(topics = "client-events-topic", groupId = "client-events-consumer")
    public void consume(List<String> messages) {
        log.info("Received messages: {}", messages.size());

        List<EventMessage> newEvents = new ArrayList<>();
        for(String message: messages) {
            try {
                newEvents.add(jsonMapper.readValue(message, EventMessage.class));
            } catch (Exception e) {
                log.error("Failed to parse/process message: {}", message, e);
            }
        }

        if(!newEvents.isEmpty()) {
            eventClickHouseRepository.insertEvents(newEvents);
            log.info("Insert {} events into ClickHouse", newEvents.size());
        }
    }
}
