ALTER TABLE game
DROP COLUMN team_home,
DROP COLUMN team_away;

ALTER TABLE game
    ADD COLUMN team_home VARCHAR(255),
    ADD COLUMN team_away VARCHAR(255),
    ADD COLUMN date_of_match TIMESTAMP,
    ADD COLUMN score_team_home INTEGER,
    ADD COLUMN score_team_away INTEGER,
    ADD COLUMN is_game_ended BOOLEAN,
    ADD COLUMN is_game_in_live BOOLEAN,
    ADD FOREIGN KEY (team_home) REFERENCES team (name_en) ON DELETE CASCADE,
    ADD FOREIGN KEY (team_away) REFERENCES team (name_en) ON DELETE CASCADE;
