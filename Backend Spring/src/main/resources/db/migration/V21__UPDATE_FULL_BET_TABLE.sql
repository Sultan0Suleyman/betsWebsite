ALTER TABLE ordinary_bet
DROP FOREIGN KEY ordinary_bet_ibfk_3,
    ADD FOREIGN KEY (full_bet_id) REFERENCES full_bet(id) ON DELETE CASCADE;

ALTER TABLE full_bet
ADD COLUMN is_bet_sold BOOLEAN,
    ADD COLUMN sell_amount DOUBLE;