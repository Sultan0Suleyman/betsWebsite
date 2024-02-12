package com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList;

public class FullBetDTO {
    private Long id;
    private Double finalCoefficient;
    private Double betAmount;
    private Double finalBetPayout;
    private Boolean betStatus;
    private Integer countOfOrdinaryBets;

    public FullBetDTO(Long id, Double finalCoefficient, Double betAmount, Double finalBetPayout, Boolean betStatus, Integer countOfOrdinaryBets) {
        this.id = id;
        this.finalCoefficient = finalCoefficient;
        this.betAmount = betAmount;
        this.finalBetPayout = finalBetPayout;
        this.betStatus = betStatus;
        this.countOfOrdinaryBets = countOfOrdinaryBets;
    }

    public Integer getCountOfOrdinaryBets() {
        return countOfOrdinaryBets;
    }

    public void setCountOfOrdinaryBets(Integer countOfOrdinaryBets) {
        this.countOfOrdinaryBets = countOfOrdinaryBets;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getFinalBetPayout() {
        return finalBetPayout;
    }

    public void setFinalBetPayout(Double finalBetPayout) {
        this.finalBetPayout = finalBetPayout;
    }

    public Boolean getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(Boolean betStatus) {
        this.betStatus = betStatus;
    }
}
