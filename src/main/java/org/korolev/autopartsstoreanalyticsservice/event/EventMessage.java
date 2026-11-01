package org.korolev.autopartsstoreanalyticsservice.event;

import java.time.Instant;

public record EventMessage(
        Long eventId,
        String eventType,
        Long userId,
        Instant eventDate,
        Integer sessionId
        //,String payload
) { }
