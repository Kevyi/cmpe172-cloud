USE cloudServiceProject;

CREATE TABLE IF NOT EXISTS `user` (
    email      VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS admin (
    email    VARCHAR(255) PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS server (
    server_id  VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255) NOT NULL,
    status     ENUM('online','offline','maintenance') NOT NULL DEFAULT 'online'
);

CREATE TABLE IF NOT EXISTS app_service (
    service_id   INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  TEXT,
    docker_image VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS availability_slot (
    server_id   VARCHAR(255) NOT NULL,
    date        DATE NOT NULL,
    status      BOOLEAN NOT NULL, -- True = Available, False = Booked
    start_time  DATETIME NOT NULL,
    end_time    DATETIME NOT NULL,
    PRIMARY KEY (server_id, start_time),
    FOREIGN KEY (server_id) REFERENCES server(server_id)
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

-- Seed data
INSERT IGNORE INTO server (server_id, name, ip_address, status) VALUES
    ('srv-001', 'Alpha Node',  '192.168.50.10', 'online'),
    ('srv-002', 'Beta Node',   '192.168.50.11', 'online'),
    ('srv-003', 'Gamma Node',  '192.168.50.12', 'maintenance');

INSERT IGNORE INTO app_service (name, description, docker_image) VALUES
    ('Nginx Web Server',  'Lightweight HTTP reverse proxy',   'nginx:latest'),
    ('PostgreSQL',        'Open-source relational database',  'postgres:15'),
    ('Redis Cache',       'In-memory key-value store',        'redis:7'),
    ('Node.js App',       'Generic Node.js runtime',          'node:20-alpine'),
    ('Python FastAPI',    'Async Python web service',         'tiangolo/uvicorn-gunicorn-fastapi:python3.11');

INSERT IGNORE INTO `user` (email, name, password, created_at) VALUES
    ('demo@example.com', 'Demo User', 'demo123', '2026-01-01');

INSERT IGNORE INTO admin (email, name, password) VALUES
    ('admin@cloudservice.local', 'Admin User', 'admin123');

INSERT IGNORE INTO availability_slot (server_id, date, status, start_time, end_time) VALUES
    ('srv-001', '2026-05-10', TRUE, '2026-05-10 09:00:00', '2026-05-10 11:00:00'),
    ('srv-001', '2026-05-10', TRUE, '2026-05-10 13:00:00', '2026-05-10 15:00:00'),
    ('srv-002', '2026-05-10', TRUE, '2026-05-10 10:00:00', '2026-05-10 12:00:00'),
    ('srv-002', '2026-05-11', TRUE, '2026-05-11 14:00:00', '2026-05-11 16:00:00');
