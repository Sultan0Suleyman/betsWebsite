package com.sobolbetbackend.backendprojektbk1.dto.placeBet;

public class FullBetRequestDTO {
    private OrdinaryBetRequestDTO[] bets;
    private Double finalCoefficient;
    private Double betAmount;
    private Long userId;

    public OrdinaryBetRequestDTO[] getBets() {
        return bets;
    }

    public void setBets(OrdinaryBetRequestDTO[] bets) {
        this.bets = bets;
    }

    public Double getFinalCoefficient() {
        return finalCoefficient;
    }

    public void setFinalCoefficient(Double finalCoefficient) {
        this.finalCoefficient = finalCoefficient;
    }

    public Double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Double betAmount) {
        this.betAmount = betAmount;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
