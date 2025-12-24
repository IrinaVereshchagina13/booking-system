ALTER TABLE tickets DROP CONSTRAINT tickets_session_id_seat_id_key;

CREATE UNIQUE INDEX tickets_unique_active_booking_idx
    ON tickets (session_id, seat_id)
    WHERE status != 'CANCELED';