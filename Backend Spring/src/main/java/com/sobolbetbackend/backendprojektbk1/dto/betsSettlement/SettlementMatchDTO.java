package com.sobolbetbackend.backendprojektbk1.dto.betsSettlement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a match in the settlement process.
 *
 * This DTO is used by the linemaker interface to display match details,
 * current scores, and settlement status.
 *
 * Contains:
 * - General match info (sport, country, league)
 * - Teams involved
 * - Match date and time
 * - Score details (main time, extra time, penalties)
 * - Status flags indicating whether the game is finished and whether results are processed
 *
 * Status fields:
 * - gameEnded: indicates if the match has been completed
 * - resultsProcessed: indicates if all bets for this match have been settled
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementMatchDTO {

    private Long id;

    private String sport;
    private String country;
    private String league;

    private String homeTeam;
    private String awayTeam;

    private LocalDateTime dateOfMatch;

    private Integer scoreHome;
    private Integer scoreAway;
    private Integer extraTimeHomeScore;
    private Integer extraTimeAwayScore;
    private Integer penaltyHomeScore;
    private Integer penaltyAwayScore;

    private Boolean gameEnded;
    private Boolean resultsProcessed;
}
