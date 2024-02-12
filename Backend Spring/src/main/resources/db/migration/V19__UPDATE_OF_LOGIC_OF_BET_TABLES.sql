CREATE TABLE IF NOT EXISTS full_bet(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL,
    final_coefficient DOUBLE,
    bet_amount DOUBLE,
    win_amount DOUBLE,
    winning_bet BOOLEAN,
    FOREIGN KEY (player_id) REFERENCES player(id)
);

ALTER TABLE bet
    DROP FOREIGN KEY bet_ibfk_1,
    DROP COLUMN player_id,
    DROP COLUMN bet_amount,
    DROP COLUMN payment_amount,
    ADD COLUMN full_bet_id BIGINT,
    ADD FOREIGN KEY (full_bet_id) REFERENCES full_bet(id),
    ADD COLUMN coefficient DOUBLE;

RENAME TABLE bet TO ordinary_bet;