package com.sobolbetbackend.backendprojektbk1.entity.events.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class Withdraw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Player player;
    private Double withdrawalAmount;
    private LocalDateTime dateOfWithdraw;
    @ManyToOne
    @JoinColumn(name = "payment_method", referencedColumnName = "name_en")
    private PaymentMethod paymentMethod;
    private Long accountNumber;
    private Boolean isPaymentSuccessful;

    public Withdraw() {
    }

    public Withdraw(Player player, Double withdrawalAmount, LocalDateTime dateOfRefill,
                    PaymentMethod paymentMethod, Long accountNumber) {
        this.player = player;
        this.withdrawalAmount = withdrawalAmount;
        this.dateOfWithdraw = dateOfRefill;
        this.paymentMethod = paymentMethod;
        this.accountNumber = accountNumber;
        this.isPaymentSuccessful = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Double getWithdrawalAmount() {
        return withdrawalAmount;
    }

    public void setWithdrawalAmount(Double withdrawalAmount) {
        this.withdrawalAmount = withdrawalAmount;
    }

    public LocalDateTime getDateOfWithdraw() {
        return dateOfWithdraw;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setDateOfWithdraw(LocalDateTime dateOfRefill) {
        this.dateOfWithdraw = dateOfRefill;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getPaymentSuccessful() {
        return isPaymentSuccessful;
    }

    public void setPaymentSuccessful(Boolean paymentSuccessful) {
        isPaymentSuccessful = paymentSuccessful;
    }
}
