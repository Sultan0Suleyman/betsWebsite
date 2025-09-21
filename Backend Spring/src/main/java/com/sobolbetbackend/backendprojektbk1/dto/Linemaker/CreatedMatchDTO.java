package com.sobolbetbackend.backendprojektbk1.dto.Linemaker;

import lombok.Data;

@Data
public class CreatedMatchDTO {
    private String sport;
    private String country;
    private String league;
    private String teamHome;
    private String teamAway;
    private String dateOfMatch;
}
