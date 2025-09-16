ALTER TABLE game
ADD COLUMN extra_time_home_score INT NULL COMMENT 'Home team score in extra time',
ADD COLUMN extra_time_away_score INT NULL COMMENT 'Away team score in extra time',
ADD COLUMN penalty_home_score INT NULL COMMENT 'Home team penalty score',
ADD COLUMN penalty_away_score INT NULL COMMENT 'Away team penalty score';