-- SBDB details per asteroid, so /api/asteroids/details reads from the database instead of calling NASA live.
-- The raw SBDB response is kept in payload; the hazard flag and names are copied out for the approaches list.
CREATE TABLE IF NOT EXISTS asteroid_details (
    designation      VARCHAR(255) PRIMARY KEY,
    full_name        VARCHAR(255),
    pha              BOOLEAN,
    neo              BOOLEAN,
    orbit_class_code VARCHAR(255),
    orbit_class_name VARCHAR(255),
    payload          TEXT         NOT NULL,
    fetched_at       TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_asteroid_details_fetched_at ON asteroid_details (fetched_at);
