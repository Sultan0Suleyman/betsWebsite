package com.sobolbetbackend.backendprojektbk1.entity.events.bet;

import com.sobolbetbackend.backendprojektbk1.entity.Player;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
public class FullBet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "fullBet", fetch = FetchType.EAGER)
    private List<OrdinaryBet> bets;
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    private Player player;
    private Double finalCoefficient;
    private Double betAmount;
    private Boolean betStatus;
    private Double finalBetPayout;

    public FullBet() {
    }

    public FullBet(List<OrdinaryBet> bets, Player player, Double finalCoefficient, Double betAmount) {
        this.bets = bets;
        this.player = player;
        this.finalCoefficient = round(finalCoefficient,4);
        this.betAmount = betAmount;
        this.betStatus = null;
        this.finalBetPayout = null;
    }


    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrdinaryBet> getBets() {
        return bets;
    }

    public void setBets(List<OrdinaryBet> bets) {
        this.bets = bets;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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

    public Boolean getBetStatus() {
        return betStatus;
    }

    public void setBetStatus(Boolean betStatus) {
        this.betStatus = betStatus;
    }

    public Double getFinalBetPayout() {
        return finalBetPayout;
    }

    public void setFinalBetPayout(Double finalBetPayout) {
        this.finalBetPayout = round(finalBetPayout,2);
    }
}

