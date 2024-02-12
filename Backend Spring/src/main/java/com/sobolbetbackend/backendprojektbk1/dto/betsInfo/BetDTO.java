package com.sobolbetbackend.backendprojektbk1.dto.betsInfo;

public class BetDTO {
    private String type;
    private Double value;

    // Конструктор и геттеры/сеттеры

    public BetDTO(String type, Double value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
