package com.sobolbetbackend.backendprojektbk1.dto.payment;

public class BalanceUpdateRequestDTO {
    private Long userId;
    private Double amount;

    public BalanceUpdateRequestDTO() {
    }

    public Long getUserId() {
        return userId;
    }

    public Double getAmount() {
        return amount;
    }
}
