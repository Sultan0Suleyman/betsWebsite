package com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class Team {
    @Id
    private String name_en;

    @ManyToOne
    @JoinColumn(name = "league", referencedColumnName = "name_en")
    private League league;

    @ManyToOne
    @JoinColumn(name = "sport", referencedColumnName = "name_en")
    private Sport sport;

    @ManyToOne
    @JoinColumn(name = "country", referencedColumnName = "name_en")
    private Country country;

    public Team(String name_en, League league, Sport sport, Country country) {
        this.name_en = name_en;
        this.league = league;
        this.sport = sport;
        this.country = country;
    }

    public Team() {
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name_en, team.name_en);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name_en);
    }
}
