USE cloudServiceProject;

CREATE TABLE IF NOT EXISTS availability_slot (
    server_id   VARCHAR(255) NOT NULL,
    date        DATE NOT NULL,
    status      BOOLEAN NOT NULL, -- True = Available, False = Booked
    start_time  DATETIME NOT NULL,
    end_time    DATETIME NOT NULL,
    PRIMARY KEY (server_id, start_time)
);

CREATE TABLE IF NOT EXISTS appointment (
    app_id      VARCHAR(255) PRIMARY KEY,
    server_id   VARCHAR(255) NOT NULL,
    service_id  VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    status      VARCHAR(50) NOT NULL,
    created_at  DATE NOT NULL,
    start_time  DATETIME NOT NULL,
    end_time    DATETIME NOT NULL,
    FOREIGN KEY (server_id, start_time) REFERENCES availability_slot(server_id, start_time)
);
