CREATE TABLE IF NOT EXISTS usere
(
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL,
                      surname VARCHAR(255) NOT NULL,
                      number_of_passport BIGINT NOT NULL,
                      passport_issue_date DATE NOT NULL,
                      passport_issuing_authority VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      created_at TIMESTAMP NOT NULL,
                      role VARCHAR(255) NOT NULL
)AUTO_INCREMENT = 1234567890;

ALTER TABLE player
    DROP COLUMN name,
    DROP COLUMN surname,
    DROP COLUMN number_of_passport,
    DROP COLUMN passport_issue_date,
    DROP COLUMN passport_issuing_authority,
    DROP COLUMN email,
    DROP COLUMN password,
    DROP COLUMN created_at;

ALTER TABLE linemaker
    DROP COLUMN name,
    DROP COLUMN surname,
    DROP COLUMN number_of_passport,
    DROP COLUMN passport_issue_date,
    DROP COLUMN passport_issuing_authority,
    DROP COLUMN email,
    DROP COLUMN password,
    DROP COLUMN created_at;

ALTER TABLE support
    DROP COLUMN name,
    DROP COLUMN surname,
    DROP COLUMN number_of_passport,
    DROP COLUMN passport_issue_date,
    DROP COLUMN passport_issuing_authority,
    DROP COLUMN email,
    DROP COLUMN password,
    DROP COLUMN created_at;

ALTER TABLE Player
    ADD COLUMN user_id BIGINT,
    ADD FOREIGN KEY (user_id) REFERENCES Usere(id) ON DELETE CASCADE;

ALTER TABLE Linemaker
    ADD COLUMN user_id BIGINT,
    ADD FOREIGN KEY (user_id) REFERENCES Usere(id) ON DELETE CASCADE;

ALTER TABLE Support
    ADD COLUMN user_id BIGINT,
    ADD FOREIGN KEY (user_id) REFERENCES Usere(id) ON DELETE CASCADE;

ALTER TABLE player AUTO_INCREMENT = 1;