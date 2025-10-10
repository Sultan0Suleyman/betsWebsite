package com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches;

import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.BetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnpublishedMatchOddsDetailsDTO {

    private Long id;
    private String sport;
    private String country;
    private String league;
    private String teamHome;
    private String teamAway;
    private String dateOfMatch;

    private Map<String, Double> odds;
}
