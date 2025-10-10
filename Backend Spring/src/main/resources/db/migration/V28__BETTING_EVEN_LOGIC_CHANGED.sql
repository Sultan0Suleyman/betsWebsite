CREATE TABLE betting_odd (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             betting_event_id BIGINT NOT NULL,
                             type VARCHAR(50) NOT NULL,
                             value DOUBLE,
                             CONSTRAINT fk_betting_event FOREIGN KEY (betting_event_id)
                                 REFERENCES betting_event(id)
                                 ON DELETE CASCADE
);

ALTER TABLE betting_event
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    DROP COLUMN win1,
    DROP COLUMN draw,
    DROP COLUMN win2,
    DROP COLUMN x1,
    DROP COLUMN w12,
    DROP COLUMN x2,
    DROP COLUMN w1in_match,
    DROP COLUMN w2in_match,
    DROP COLUMN first_team_scores_first,
    DROP COLUMN second_team_scores_first;