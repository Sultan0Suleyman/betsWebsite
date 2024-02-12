package com.sobolbetbackend.backendprojektbk1.dto.placeBet;

public class OrdinaryBetRequestDTO {
    private Long matchId;
    private String type;
    private Double coefficient;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }
}
