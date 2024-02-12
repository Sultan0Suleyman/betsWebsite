package com.sobolbetbackend.backendprojektbk1.dto.payment.Refill;


public class RefillRequestDTO {
    private Long userId;
    private String isPaymentSuccessful;
    private String paymentMethod;
    private Double amount;

    public RefillRequestDTO() {
    }

    public Long getUserId() {
        return userId;
    }

    public String getIsPaymentSuccessful() {
        return isPaymentSuccessful;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }
}
