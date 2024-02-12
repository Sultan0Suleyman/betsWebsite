package com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo;

import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEvent;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "game")
    private BettingEvent bettingEvent;
    @ManyToOne
    @JoinColumn(name = "team_home", referencedColumnName = "name_en")
    private Team teamHome;
    @ManyToOne
    @JoinColumn(name = "team_away", referencedColumnName = "name_en")
    private Team teamAway;
    @ManyToOne
    @JoinColumn(name = "league", referencedColumnName = "name_en")
    private League league;
    @ManyToOne
    @JoinColumn(name = "sport", referencedColumnName = "name_en")
    private Sport sport;

    @ManyToOne
    @JoinColumn(name = "country", referencedColumnName = "name_en")
    private Country country;
    private LocalDateTime dateOfMatch;
    private Integer scoreTeamHome;
    private Integer scoreTeamAway;
    private Boolean isGameEnded;
    private Boolean isGameInLive;
    private Boolean isGamePosted;
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

    public Team getTeamHome() {
        return teamHome;
    }

    public void setTeamHome(Team teamHome) {
        this.teamHome = teamHome;
    }

    public Team getTeamAway() {
        return teamAway;
    }

    public void setTeamAway(Team teamAway) {
        this.teamAway = teamAway;
    }

    public LocalDateTime getDateOfMatch() {
        return dateOfMatch;
    }

    public void setDateOfMatch(LocalDateTime dateOfMatch) {
        this.dateOfMatch = dateOfMatch;
    }

    public Integer getScoreTeamHome() {
        return scoreTeamHome;
    }

    public void setScoreTeamHome(Integer scoreTeamHome) {
        this.scoreTeamHome = scoreTeamHome;
    }

    public Integer getScoreTeamAway() {
        return scoreTeamAway;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public void setScoreTeamAway(Integer scoreTeamAway) {
        this.scoreTeamAway = scoreTeamAway;
    }

    public BettingEvent getBettingEvent() {
        return bettingEvent;
    }

    public void setBettingEvent(BettingEvent bettingEvent) {
        this.bettingEvent = bettingEvent;
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
