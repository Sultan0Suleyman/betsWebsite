package com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo;

import jakarta.persistence.*;

@Entity
public class League {
    @Id
    private String name_en;

    @ManyToOne
    @JoinColumn(name = "country", referencedColumnName = "name_en")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "sport", referencedColumnName = "name_en")
    private Sport sport;

    public League(String name_en, Country country, Sport sport) {
        this.name_en = name_en;
        this.country = country;
        this.sport = sport;
    }

    public League() {
    }

    public String getName() {
        return name_en;
    }

    public void setName(String name_en) {
        this.name_en = name_en;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }
}
