-- Create Schema and User Role Type
CREATE
    TYPE user_role AS ENUM ('USER', 'ADMIN');

-- Users Table
CREATE TABLE IF NOT EXISTS "user"
(
    id
    SERIAL
    PRIMARY
    KEY,
    first_name
    VARCHAR
(
    50
) NOT NULL,
    last_name VARCHAR
(
    50
) NOT NULL,
    nickname VARCHAR
(
    255
) UNIQUE,
    user_role user_role NOT NULL,
    avatar BYTEA,
    talent_points INT DEFAULT 50,
    email_address VARCHAR
(
    255
) UNIQUE NOT NULL,
    password_hash VARCHAR
(
    512
) NOT NULL,
    password_salt VARCHAR
(
    512
) NOT NULL,
    hash_method VARCHAR
(
    20
) NOT NULL,
    secret_for_email_verification VARCHAR
(
    512
),
    secret_for_password_reset VARCHAR
(
    512
),
    has_admin_verified BOOLEAN DEFAULT FALSE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    phone_number VARCHAR
(
    20
),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW
(
),
    updated_at TIMESTAMP
                         WITHOUT TIME ZONE,
    country VARCHAR
(
    40
) NOT NULL,
    city VARCHAR
(
    150
) NOT NULL,
    postal_code VARCHAR
(
    25
) NOT NULL,
    street VARCHAR
(
    150
),
    address_addition VARCHAR
(
    40
),
    house_number VARCHAR
(
    10
),
    CONSTRAINT name_check CHECK
(
    length
(
    first_name
) > 0 AND length
(
    last_name
) > 0)
    );


-- System Settings Table
CREATE TABLE IF NOT EXISTS system_settings
(
    id
    SERIAL
    PRIMARY
    KEY,
    css_name
    VARCHAR
(
    255
),
    logo BYTEA,
    data_protection TEXT,
    max_pic_size INT,
    contact_info TEXT,
    is_admin_confirmation_needed_registration BOOLEAN,
    imprint TEXT,
    sum_paginated_items INT
    );


-- Ads Table
CREATE TABLE IF NOT EXISTS ad
(
    id
    SERIAL
    PRIMARY
    KEY,
    creator_id
    INT
    NOT
    NULL
    REFERENCES
    "user"
(
    id
) ON DELETE CASCADE,
    title VARCHAR
(
    255
),
    image BYTEA,
    free_text VARCHAR
(
    2047
),
    cost_in_talent_points INT NOT NULL CHECK
(
    cost_in_talent_points
    >=
    0
),
    date_of_publication DATE NOT NULL,
    date_of_completion DATE,
    is_active BOOLEAN DEFAULT TRUE,
    country VARCHAR
(
    40
) NOT NULL,
    city VARCHAR
(
    150
) NOT NULL,
    postal_code VARCHAR
(
    25
) NOT NULL,
    street VARCHAR
(
    150
),
    address_addition VARCHAR
(
    40
),
    house_number VARCHAR
(
    10
),
    phone_number VARCHAR
(
    40
),
    email_address VARCHAR
(
    255
) NOT NULL,
    street_shown BOOLEAN,
    name_shown BOOLEAN,
    phone_number_shown BOOLEAN
    );

-- Requests/Responses Table
CREATE TABLE IF NOT EXISTS request_response
(
    id
    SERIAL
    PRIMARY
    KEY,
    ad_id
    INT
    NOT
    NULL
    REFERENCES
    ad
(
    id
) ON DELETE CASCADE,
    request_creator_id INT REFERENCES "user"
(
    id
)
  ON DELETE SET NULL,
    timestamp_request TIMESTAMP
  WITHOUT TIME ZONE DEFAULT NOW
(
),
    timestamp_response TIMESTAMP
  WITHOUT TIME ZONE,
    free_text_request TEXT,
    free_text_response TEXT,
    result BOOLEAN
    );

CREATE UNIQUE INDEX email_unique_case_insensitive
    ON "user" (LOWER(email_address));

CREATE UNIQUE INDEX nickname_unique_case_insensitive
    ON "user" (LOWER(nickname));
