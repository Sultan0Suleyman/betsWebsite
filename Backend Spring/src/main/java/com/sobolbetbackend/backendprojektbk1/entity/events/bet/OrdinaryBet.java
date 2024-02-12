package com.sobolbetbackend.backendprojektbk1.entity.events.bet;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class OrdinaryBet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "full_bet_id", referencedColumnName = "id", nullable = false)
    private FullBet fullBet;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id", nullable = false)
    private Game game;
    private String outcomeOfTheGame;
    private Double coefficient;
    private Boolean winningBet;

    public OrdinaryBet() {
    }

    public OrdinaryBet(FullBet fullBet, Game game, String outcomeOfTheGame, Double coefficient) {
        this.fullBet = fullBet;
        this.game = game;
        this.outcomeOfTheGame = outcomeOfTheGame;
        this.coefficient = coefficient;
        this.winningBet = null;
    }

    public boolean isWinningBet() {
        return winningBet;
    }

    public FullBet getFullBet() {
        return fullBet;
    }

    public void setFullBet(FullBet fullBet) {
        this.fullBet = fullBet;
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

    public void setWinningBet(boolean winningBet) {
        this.winningBet = winningBet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getOutcomeOfTheGame() {
        return outcomeOfTheGame;
    }

    public void setOutcomeOfTheGame(String outcomeOfTheGame) {
        this.outcomeOfTheGame = outcomeOfTheGame;
    }

}
