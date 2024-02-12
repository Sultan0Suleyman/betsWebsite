ALTER TABLE refill
    ADD COLUMN date_of_refill TIMESTAMP,
    ADD COLUMN is_payment_successful BOOLEAN,
    ADD COLUMN payment_method VARCHAR(255),
    ADD FOREIGN KEY (payment_method) REFERENCES payment_method(name_en)