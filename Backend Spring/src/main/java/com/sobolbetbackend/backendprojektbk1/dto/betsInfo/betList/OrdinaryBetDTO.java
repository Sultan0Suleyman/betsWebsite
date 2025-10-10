package com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class OrdinaryBetDTO {
    private String teamHome;
    private String teamAway;
    private LocalDateTime dateOfMatch;
    private String outcomeOfTheGame;
    private Double coefficient;
    private Boolean winningBet;
    @JsonProperty("isGameEnded")
    private boolean isGameEnded;

    public OrdinaryBetDTO(String teamHome, String teamAway,
                          LocalDateTime dateOfMatch, String outcomeOfTheGame,
                          Double coefficient, Boolean winningBet,
                          boolean isGameEnded) {
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.dateOfMatch = dateOfMatch;
        this.outcomeOfTheGame = outcomeOfTheGame;
        this.coefficient = coefficient;
        this.winningBet = winningBet;
        this.isGameEnded = isGameEnded;
    }

    public String getTeamHome() {
        return teamHome;
    }

    public void setTeamHome(String teamHome) {
        this.teamHome = teamHome;
    }

    public String getTeamAway() {
        return teamAway;
    }

    public void setTeamAway(String teamAway) {
        this.teamAway = teamAway;
    }

    public LocalDateTime getDateOfMatch() {
        return dateOfMatch;
    }

    public void setDateOfMatch(LocalDateTime dateOfMatch) {
        this.dateOfMatch = dateOfMatch;
    }

    public String getOutcomeOfTheGame() {
        return outcomeOfTheGame;
    }

    public void setOutcomeOfTheGame(String outcomeOfTheGame) {
        this.outcomeOfTheGame = outcomeOfTheGame;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Boolean getWinningBet() {
        return winningBet;
    }

    public void setWinningBet(Boolean winningBet) {
        this.winningBet = winningBet;
    }

    public boolean getGameEnded() {
        return isGameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        isGameEnded = gameEnded;
    }
}
