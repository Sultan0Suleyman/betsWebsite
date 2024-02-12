package com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Sport {
    @Id
    private String name_en;

    public Sport() {
    }

    public Sport(String name_en) {
        this.name_en = name_en;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }
}
