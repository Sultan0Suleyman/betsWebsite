package com.sobolbetbackend.backendprojektbk1.entity.events.bet;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import jakarta.persistence.*;

@Entity
public class BettingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;
    private Double win1;
    private Double draw;
    private Double win2;
    private Double X1;
    private Double W12;
    private Double X2;
    private Double W1InMatch;
    private Double W2InMatch;
    private Double firstTeamScoresFirst;
    private Double secondTeamScoresFirst;

    public BettingEvent(Game game, Double win1,
                        Double draw, Double win2, Double x1,
                        Double w12, Double x2, Double w1InMatch, Double w2InMatch, Double firstTeamScoresFirst, Double secondTeamScoresFirst) {
        this.game = game;
        this.win1 = win1;
        this.draw = draw;
        this.win2 = win2;
        X1 = x1;
        W12 = w12;
        X2 = x2;
        W1InMatch = w1InMatch;
        W2InMatch = w2InMatch;
        this.firstTeamScoresFirst = firstTeamScoresFirst;
        this.secondTeamScoresFirst = secondTeamScoresFirst;
    }

    public BettingEvent() {
    }

    public Double getFirstTeamScoresFirst() {
        return firstTeamScoresFirst;
    }

    public void setFirstTeamScoresFirst(Double firstTeamScoresFirst) {
        this.firstTeamScoresFirst = firstTeamScoresFirst;
    }

    public Double getSecondTeamScoresFirst() {
        return secondTeamScoresFirst;
    }

    public void setSecondTeamScoresFirst(Double secondTeamScoresFirst) {
        this.secondTeamScoresFirst = secondTeamScoresFirst;
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

    public Double getWin1() {
        return win1;
    }

    public void setWin1(Double win1) {
        this.win1 = win1;
    }

    public Double getDraw() {
        return draw;
    }

    public void setDraw(Double draw) {
        this.draw = draw;
    }

    public Double getWin2() {
        return win2;
    }

    public void setWin2(Double win2) {
        this.win2 = win2;
    }

    public Double getX1() {
        return X1;
    }

    public void setX1(Double x1) {
        X1 = x1;
    }

    public Double getW12() {
        return W12;
    }

    public void setW12(Double w12) {
        W12 = w12;
    }

    public Double getX2() {
        return X2;
    }

    public void setX2(Double x2) {
        X2 = x2;
    }

    public Double getW1InMatch() {
        return W1InMatch;
    }

    public void setW1InMatch(Double w1InMatch) {
        W1InMatch = w1InMatch;
    }

    public Double getW2InMatch() {
        return W2InMatch;
    }

    public void setW2InMatch(Double w2InMatch) {
        W2InMatch = w2InMatch;
    }
}
