package com.sobolbetbackend.backendprojektbk1.dto.betsSettlement;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object used to submit final match results from the linemaker.
 *
 * Contains the main score as well as optional extra time and penalty scores.
 * This DTO is used when finishing a game and settling all related bets.
 *
 * Fields:
 * - homeScore / awayScore: main time score (required)
 * - extraTimeHomeScore / extraTimeAwayScore: extra time score (optional)
 * - penaltyHomeScore / penaltyAwayScore: penalty shootout score (optional)
 *
 * All additional scores are nullable and only used if applicable for the sport.
 */
@Getter
@Setter
public class FinishGameRequestDTO {
    private Integer homeScore;
    private Integer awayScore;
    private Integer extraTimeHomeScore;
    private Integer extraTimeAwayScore;
    private Integer penaltyHomeScore;
    private Integer penaltyAwayScore;
}
