package com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class SetOddsRequestDTO {
    private Long matchId;
    private Map<String, Double> odds;
}
