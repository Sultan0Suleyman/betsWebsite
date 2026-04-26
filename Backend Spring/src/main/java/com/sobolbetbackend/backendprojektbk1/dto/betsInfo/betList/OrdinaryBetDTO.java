package com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrdinaryBetDTO {
    private String teamHome;
    private String teamAway;
    private LocalDateTime dateOfMatch;
    private String outcomeOfTheGame;
    private Double coefficient;
    private Boolean winningBet;

    @JsonProperty("isGameEnded")
    private boolean isGameEnded;

    private Integer scoreHome;
    private Integer scoreAway;
    private Integer extraTimeHomeScore;
    private Integer extraTimeAwayScore;
    private Integer penaltyHomeScore;
    private Integer penaltyAwayScore;

    public OrdinaryBetDTO(String teamHome,
                          String teamAway,
                          LocalDateTime dateOfMatch,
                          String outcomeOfTheGame,
                          Double coefficient,
                          Boolean winningBet,
                          boolean isGameEnded,
                          Integer scoreHome,
                          Integer scoreAway,
                          Integer extraTimeHomeScore,
                          Integer extraTimeAwayScore,
                          Integer penaltyHomeScore,
                          Integer penaltyAwayScore) {
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.dateOfMatch = dateOfMatch;
        this.outcomeOfTheGame = outcomeOfTheGame;
        this.coefficient = coefficient;
        this.winningBet = winningBet;
        this.isGameEnded = isGameEnded;
        this.scoreHome = scoreHome;
        this.scoreAway = scoreAway;
        this.extraTimeHomeScore = extraTimeHomeScore;
        this.extraTimeAwayScore = extraTimeAwayScore;
        this.penaltyHomeScore = penaltyHomeScore;
        this.penaltyAwayScore = penaltyAwayScore;
    }
}
