package com.sobolbetbackend.backendprojektbk1.entity.other;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RevokedRefreshToken {
    @Id
    private String token;

    public RevokedRefreshToken() {
    }

    public RevokedRefreshToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
