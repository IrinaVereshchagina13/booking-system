CREATE TABLE cinemas (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL
);

CREATE TABLE halls (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cinema_id BIGINT NOT NULL REFERENCES cinemas(id)
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL
);

CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT NOT NULL REFERENCES halls(id),
    row_number INT NOT NULL,
    seat_number INT NOT NULL,
    UNIQUE (hall_id, row_number, seat_number)
);

CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    hall_id BIGINT NOT NULL REFERENCES halls(id),
    movie_id BIGINT NOT NULL REFERENCES movies(id),
    start_time TIMESTAMP NOT NULL,
    price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL, -- Пока просто ID, без таблицы пользователей
    session_id BIGINT NOT NULL REFERENCES sessions(id),
    seat_id BIGINT NOT NULL REFERENCES seats(id),
    status VARCHAR(50) NOT NULL, -- BOOKED, PAID, CANCELED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (session_id, seat_id)
);