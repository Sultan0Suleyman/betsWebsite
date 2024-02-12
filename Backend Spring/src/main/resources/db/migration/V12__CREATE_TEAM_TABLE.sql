CREATE TABLE IF NOT EXISTS team (
                                      name_en VARCHAR(255) PRIMARY KEY,
                                      league VARCHAR(255),
                                      sport VARCHAR(255),
                                      country VARCHAR(255),
                                      FOREIGN KEY (country) REFERENCES country (name_en) ON DELETE CASCADE,
                                      FOREIGN KEY (sport) REFERENCES sport (name_en) ON DELETE CASCADE,
                                      FOREIGN KEY (league) REFERENCES league (name_en) ON DELETE CASCADE
);