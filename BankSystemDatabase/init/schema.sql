CREATE TABLE user_information (
    user_ID SERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    user_password VARCHAR(255) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_surname VARCHAR(100) NOT NULL,
    user_salary NUMERIC(12,2),
    user_birthday DATE NOT NULL,
    user_id_card_no_series VARCHAR(3) NOT NULL,
    user_id_card_no VARCHAR(10) NOT NULL UNIQUE,
    user_fin VARCHAR(7) NOT NULL UNIQUE,
    user_phone_number VARCHAR(13) NOT NULL UNIQUE,
    user_codeword VARCHAR(255) NOT NULL
);

CREATE TABLE debit_card_information (
    d_card_ID BIGINT PRIMARY KEY,
    user_ID BIGINT REFERENCES user_information(user_ID) ON DELETE CASCADE,
    d_balance NUMERIC(12,2) DEFAULT 0,
    d_ppn VARCHAR(20),
    d_currency VARCHAR(3),
    d_cvv VARCHAR(3),
    d_expiry_date VARCHAR(5),
    d_status VARCHAR(10),
    d_pin VARCHAR(4)
);

CREATE TABLE credit_card_information (
    c_card_ID BIGINT PRIMARY KEY,
    user_ID BIGINT REFERENCES user_information(user_ID) ON DELETE CASCADE,
    c_balance NUMERIC(12,2) DEFAULT 0,
    c_loan_amount NUMERIC(12,2) DEFAULT 0,
    c_interest_rate NUMERIC(4,2),
    c_current_debt NUMERIC(12,2),
    c_ppn VARCHAR(20),
    c_currency VARCHAR(3),
    c_cvv VARCHAR(3),
    c_expiry_date VARCHAR(5),
    c_status VARCHAR(10),
    c_pin VARCHAR(4)
);

CREATE TABLE cashback_information (
    cb_ID SERIAL PRIMARY KEY,
    user_ID BIGINT REFERENCES user_information(user_ID) ON DELETE CASCADE,
    cb_balance NUMERIC(12,2) DEFAULT 0
);

CREATE TABLE transaction_history (
    tr_id SERIAL PRIMARY KEY,
    tr_sender_id BIGINT,
    tr_sender_user_id BIGINT,
    tr_receiver_id BIGINT,
    tr_receiver_user_id BIGINT,
    tr_type VARCHAR(100) NOT NULL,
    tr_amount NUMERIC(12,2) NOT NULL,
    tr_fee NUMERIC(12,2),
    tr_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE service (
    service_id SERIAL PRIMARY KEY,
    service_name VARCHAR(100),
    service_type VARCHAR(100),
    service_description VARCHAR(500)
);

CREATE TABLE credit_card_statements (
    statement_id VARCHAR(50) PRIMARY KEY,

    user_ID BIGINT REFERENCES user_information(user_ID) ON DELETE CASCADE,

    c_card_id BIGINT REFERENCES credit_card_information(c_card_ID) ON DELETE CASCADE,

    statement_date DATE NOT NULL,
    
    due_date DATE NOT NULL,

    opening_balance NUMERIC(12,2) DEFAULT 0,
    closing_balance NUMERIC(12,2) DEFAULT 0,

    min_payment_due NUMERIC(12,2) DEFAULT 0,
    total_payment_due NUMERIC(12,2) DEFAULT 0,

    interest_charged NUMERIC(12,2) DEFAULT 0,
    fees_charged NUMERIC(12,2) DEFAULT 0,

    purchases NUMERIC(12,2) DEFAULT 0,
    payments NUMERIC(12,2) DEFAULT 0,

    status VARCHAR(20) NOT NULL
);

CREATE TABLE otp (
    otp_id SERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL UNIQUE,
    otp_code INT,
    created_at TIMESTAMP,
    attempts INT,
    first_attempt_at TIMESTAMP,
    blocked_until TIMESTAMP
);

INSERT INTO service (service_name, service_type, service_description)
VALUES 
('Azerqaz', 'Gas', 'An entity that receives natural gas from supply points, distributes it, and sells it within the territory of the Republic of Azerbaijan.'),
('Azersu', 'Water', 'Azersu Open Joint Stock Company is in charge of policy and strategy for the water supply and sanitation services in Azerbaijan.'),
('Azerishiq', 'Electricity', 'Azerishiq is the Baku electrical grid operator.');