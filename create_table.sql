-- Veritabanı Tabloları
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    is_corporate BOOLEAN DEFAULT false,
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'CUSTOMER')) NOT NULL
);

CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    category VARCHAR(20) CHECK (category IN ('CAR', 'HELICOPTER', 'MOTORCYCLE')) NOT NULL,
    daily_price DECIMAL(15,2) NOT NULL,
    hourly_price DECIMAL(15,2) NOT NULL,
    weekly_price DECIMAL(15,2) NOT NULL,
    monthly_price DECIMAL(15,2) NOT NULL,
    cost DECIMAL(15,2) NOT NULL,
    available BOOLEAN DEFAULT true
);

CREATE TABLE rentals (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    vehicle_id INT REFERENCES vehicles(id),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    duration_type VARCHAR(20) CHECK (duration_type IN ('HOUR', 'DAY', 'WEEK', 'MONTH')) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    deposit DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
