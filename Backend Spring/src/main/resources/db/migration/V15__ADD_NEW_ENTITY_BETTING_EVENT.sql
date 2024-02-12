CREATE TABLE IF NOT EXISTS betting_event(
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    win1 DOUBLE,
    draw DOUBLE,
    win2 DOUBLE,
    X1 DOUBLE,
    W12 DOUBLE,
    X2 DOUBLE,
    w1in_match DOUBLE,
    w2in_match DOUBLE,
    first_team_scores_first DOUBLE,
    second_team_scores_first DOUBLE,
    game_id BIGINT,
    FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

ALTER TABLE game
    ADD COLUMN is_game_posted BOOLEAN;
