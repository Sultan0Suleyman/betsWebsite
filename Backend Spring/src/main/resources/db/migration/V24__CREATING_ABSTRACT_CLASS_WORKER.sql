DROP TABLE IF EXISTS contract;
DROP TABLE IF EXISTS linemaker;
DROP TABLE IF EXISTS support;
DROP TABLE IF EXISTS worker;

CREATE TABLE worker (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT,
                        worker_type VARCHAR(255),
                        CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES usere(id)
);

-- Создаем таблицу для линейных работников
CREATE TABLE linemaker (
                           id BIGINT PRIMARY KEY,
                           CONSTRAINT fk_linemaker FOREIGN KEY (id) REFERENCES worker(id)
);

-- Создаем таблицу для поддержки
CREATE TABLE support (
                         id BIGINT PRIMARY KEY,
                         CONSTRAINT fk_support FOREIGN KEY (id) REFERENCES worker(id)
);

-- Создаем таблицу для контрактов
CREATE TABLE contract (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          worker_id BIGINT NOT NULL,
                          created_at DATE NOT NULL,
                          salary DOUBLE,
                          valid_until DATE NOT NULL,
                          CONSTRAINT fk_contract_worker FOREIGN KEY (worker_id) REFERENCES worker(id)
);