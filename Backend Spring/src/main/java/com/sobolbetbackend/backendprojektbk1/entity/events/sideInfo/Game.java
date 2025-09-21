package com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo;

import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEvent;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Game {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @Getter
    @OneToOne(mappedBy = "game")
    private BettingEvent bettingEvent;
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "team_home", referencedColumnName = "name_en")
    private Team teamHome;
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "team_away", referencedColumnName = "name_en")
    private Team teamAway;
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "league", referencedColumnName = "name_en")
    private League league;
    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "sport", referencedColumnName = "name_en")
    private Sport sport;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "country", referencedColumnName = "name_en")
    private Country country;
    @Setter
    @Getter
    private LocalDateTime dateOfMatch;
    @Setter
    @Getter
    private Integer scoreTeamHome;
    @Setter
    @Getter
    private Integer scoreTeamAway;
    @Setter
    @Getter
    private Integer extraTimeHomeScore; // Счет в доп.время
    @Setter
    @Getter
    private Integer extraTimeAwayScore;
    @Setter
    @Getter
    private Integer penaltyHomeScore;   // Счет по пенальти
    @Setter
    @Getter
    private Integer penaltyAwayScore;
    private Boolean isGameEnded;
    private Boolean isGameInLive;
    private Boolean isGamePosted;
    @Setter
    @Getter
    @OneToMany(mappedBy = "game")
    private List<OrdinaryBet> bets;

    public Game() {
    }

    public Game(Team teamHome, Team teamAway, LocalDateTime dateOfMatch, League league,
                Sport sport, Country country) {
        this.teamHome = teamHome;
        this.teamAway = teamAway;
        this.dateOfMatch = dateOfMatch;
        this.league = league;
        this.sport = sport;
        this.country = country;
        this.scoreTeamHome = null;
        this.scoreTeamAway = null;
        this.isGameEnded = false;
        this.isGameInLive = false;
        this.isGamePosted = false;
        this.bettingEvent = null;
    }

    public Boolean getGamePosted() {
        return isGamePosted;
    }

    public void setGamePosted(Boolean gamePosted) {
        isGamePosted = gamePosted;
    }

    public Boolean getGameEnded() {
        return isGameEnded;
    }

    public void setGameEnded(Boolean gameEnded) {
        isGameEnded = gameEnded;
    }

    public Boolean getGameInLive() {
        return isGameInLive;
    }

    public void setGameInLive(Boolean gameInLive) {
        isGameInLive = gameInLive;
    }
    public boolean isSameGame(Game otherGame) {
        // Check if team names and date match
        return teamHome.equals(otherGame.teamHome) &&
                teamAway.equals(otherGame.teamAway) &&
                dateOfMatch.equals(otherGame.dateOfMatch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(teamHome, game.teamHome) &&
                Objects.equals(teamAway, game.teamAway) &&
                Objects.equals(league, game.league) &&
                Objects.equals(sport, game.sport) &&
                Objects.equals(country, game.country) &&
                Objects.equals(dateOfMatch, game.dateOfMatch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamHome, teamAway, league, sport, country, dateOfMatch);
    }
}
