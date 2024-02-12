package com.sobolbetbackend.backendprojektbk1.entity.other;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PaymentMethod {
    @Id
    private String name_en;

    public PaymentMethod() {
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }
}
