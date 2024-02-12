ALTER TABLE withdraw
    ADD COLUMN date_of_withdraw TIMESTAMP,
    ADD COLUMN is_payment_successful BOOLEAN,
    ADD COLUMN payment_method VARCHAR(255),
    Add COLUMN account_number BIGINT,
    ADD FOREIGN KEY (payment_method) REFERENCES payment_method(name_en)