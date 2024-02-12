package com.sobolbetbackend.backendprojektbk1.dto.payment.Refill;

public class RefillResponseDTO {
    private Boolean isPaymentSuccessful;
    private Double replenishmentAmount;

    public RefillResponseDTO(Boolean isPaymentSuccessful, Double replenishmentAmount) {
        this.isPaymentSuccessful = isPaymentSuccessful;
        this.replenishmentAmount = replenishmentAmount;
    }

    public Boolean getPaymentSuccessful() {
        return isPaymentSuccessful;
    }

    public void setPaymentSuccessful(Boolean paymentSuccessful) {
        isPaymentSuccessful = paymentSuccessful;
    }

    public Double getReplenishmentAmount() {
        return replenishmentAmount;
    }

    public void setReplenishmentAmount(Double replenishmentAmount) {
        this.replenishmentAmount = replenishmentAmount;
    }
}
