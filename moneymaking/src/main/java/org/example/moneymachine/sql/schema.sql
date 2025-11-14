CREATE TABLE IF NOT EXISTS users (
    -- 16‑character card number – serves as a natural primary key
    card_number   VARCHAR(16) PRIMARY KEY,

    -- Number of consecutive failed PIN attempts (0‑3 in the demo)
    failed_attempts INT NOT NULL CHECK (failed_attempts BETWEEN 0 AND 3),

    -- Account balance – two decimal places, up to 9 digits total
    balance        DECIMAL(10,2) NOT NULL CHECK (balance >= 0),

    -- PIN stored as a short string (the demo uses two‑digit placeholders)
    pin            VARCHAR(8) NOT NULL,

    -- Lock flag – true when failed_attempts = 3
    is_locked      BOOLEAN NOT NULL
);