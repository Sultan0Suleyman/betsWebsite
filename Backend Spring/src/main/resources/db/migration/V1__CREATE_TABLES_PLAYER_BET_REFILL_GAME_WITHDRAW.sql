CREATE TABLE IF NOT EXISTS player
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    number_of_passport BIGINT NOT NULL,
    passport_issue_date DATE NOT NULL,
    passport_issuing_authority VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    balance DOUBLE NOT NULL,
    created_at TIMESTAMP NOT NULL
)AUTO_INCREMENT = 1234567890;

CREATE TABLE IF NOT EXISTS game
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    team_home VARCHAR(255) NOT NULL,
    team_away VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS bet
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    outcome_of_the_game VARCHAR(255) NOT NULL,
    bet_amount DOUBLE NOT NULL,
    payment_amount DOUBLE NOT NULL,
    winning_bet BOOLEAN NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id),
    FOREIGN KEY (game_id) REFERENCES game(id)
);

CREATE TABLE IF NOT EXISTS refill
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL,
    replenishment_amount DOUBLE NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id)
);
CREATE TABLE IF NOT EXISTS withdraw
(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL,
    withdrawal_amount DOUBLE NOT NULL,
    FOREIGN KEY (player_id) REFERENCES player(id)
);
