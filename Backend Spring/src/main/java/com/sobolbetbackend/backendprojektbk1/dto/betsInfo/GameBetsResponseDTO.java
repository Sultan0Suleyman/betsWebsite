package com.sobolbetbackend.backendprojektbk1.dto.betsInfo;

import java.time.LocalDateTime;
import java.util.List;

public class GameBetsResponseDTO {
    private Long id;
    private String teamHome;
    private String teamAway;
    private LocalDateTime dateOfMatch;
    private List<BetDTO> bets;

    public GameBetsResponseDTO(Long id, String teamHome, String teamAway,
                               LocalDateTime dateOfMatch, List<BetDTO> bets) {
        this.id = id;
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.dateOfMatch = dateOfMatch;
        this.bets = bets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<BetDTO> getBets() {
        return bets;
    }

    public void setBets(List<BetDTO> bets) {
        this.bets = bets;
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

}
