CREATE TABLE IF NOT EXISTS linemaker
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    number_of_passport BIGINT NOT NULL,
    passport_issue_date DATE NOT NULL,
    passport_issuing_authority VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE TABLE IF NOT EXISTS support
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    number_of_passport BIGINT NOT NULL,
    passport_issue_date DATE NOT NULL,
    passport_issuing_authority VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
CREATE TABLE IF NOT EXISTS contract
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    linemaker_id BIGINT NOT NULL,
    support_id BIGINT NOT NULL,
    created_at DATE NOT NULL,
    salary DOUBLE NOT NULL,
    FOREIGN KEY (linemaker_id) REFERENCES linemaker(id),
    FOREIGN KEY (support_id) REFERENCES support(id)
);