package com.sobolbetbackend.backendprojektbk1.dto.payment.Withdraw;

public class WithdrawRequestDTO {
    private String paymentMethod;
    private Double amount;
    private Long userId;
    private Long accountNumber;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }
}
