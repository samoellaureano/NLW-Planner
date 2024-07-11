CREATE TABLE participants
(
    id           UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    trip_id      UUID,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    is_confirmed BOOLEAN      NOT NULL,
    FOREIGN KEY (trip_id) REFERENCES trips (id) ON DELETE CASCADE
);