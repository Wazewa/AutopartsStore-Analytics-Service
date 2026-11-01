package org.korolev.autopartsstoreanalyticsservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static java.sql.Types.BIGINT;

@Repository
@RequiredArgsConstructor
public class EventClickHouseRepository {
    private final JdbcTemplate jdbcTemplate;

    public void insertEvents(List<EventMessage> events) {
        String sql = "INSERT INTO event (event_id, event_type, user_id, event_date, session_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, events, events.size(), (ps, event) ->
        {
            ps.setLong(1, event.eventId());
            ps.setString(2, event.eventType());
            if(event.userId() == null) {
                ps.setNull(3, BIGINT);
            }
            else {
                ps.setLong(3, event.userId());
            }
            ps.setObject(4, LocalDateTime.ofInstant(event.eventDate(), ZoneOffset.UTC));
            ps.setInt(5, event.sessionId());
        });

    }
}
