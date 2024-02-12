package com.sobolbetbackend.backendprojektbk1.dto;

public class OnRefreshResponseDTO {
    public String accessToken;
    public String tokenType = "Bearer ";

    public OnRefreshResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}

