CREATE TABLE IF NOT EXISTS admins (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS properties (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(150)   NOT NULL,
    description VARCHAR(1000),
    city        VARCHAR(100)   NOT NULL,
    address     VARCHAR(255),
    type        VARCHAR(30)    NOT NULL, 
    price       DECIMAL(15, 2) NOT NULL,
    bedrooms    INT,
    bathrooms   INT,
    area_sqft   DOUBLE,
    status      VARCHAR(20)    NOT NULL,  
    created_at  TIMESTAMP      NOT NULL
);

CREATE TABLE IF NOT EXISTS inquiries (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL,
    phone       VARCHAR(20)  NOT NULL,
    message     VARCHAR(1000),
    created_at  TIMESTAMP    NOT NULL,
    CONSTRAINT fk_inquiry_property FOREIGN KEY (property_id) REFERENCES properties (id)
);
