package com.sobolbetbackend.backendprojektbk1.dto.Linemaker;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreatedMatchDTO {
    private String sport;
    private String country;
    private String league;
    private String teamHome;
    private String teamAway;
    private String dateOfMatch;

    public CreatedMatchDTO(String sport, String country, String league, String teamHome, String teamAway, String dateOfMatch) {
        this.sport = sport;
        this.country = country;
        this.league = league;
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.dateOfMatch = dateOfMatch;
    }
}
