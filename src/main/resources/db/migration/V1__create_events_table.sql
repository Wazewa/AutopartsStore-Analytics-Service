CREATE TABLE IF NOT EXISTS event (
    event_id UInt64,
    event_type String,
    user_id Nullable(UInt64),
    event_date DateTime,
    session_id UInt32
) ENGINE = ReplacingMergeTree()
ORDER BY event_id;