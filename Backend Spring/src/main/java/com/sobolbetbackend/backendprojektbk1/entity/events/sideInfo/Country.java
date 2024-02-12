package com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Country {
    @Id
    private String name_en;

    public Country(String name_en) {
        this.name_en = name_en;
    }

    public Country() {

    }

    public String getName() {
        return name_en;
    }

    public void setName(String name_en) {
        this.name_en = name_en;
    }
}
