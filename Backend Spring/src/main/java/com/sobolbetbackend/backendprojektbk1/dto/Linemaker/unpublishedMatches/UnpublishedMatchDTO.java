package com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnpublishedMatchDTO {
    private String id;
    private String sport;
    private String country;
    private String league;
    private String teamHome;
    private String teamAway;
    private String dateOfMatch;
    private String status;
    private String linemakersName;
}
