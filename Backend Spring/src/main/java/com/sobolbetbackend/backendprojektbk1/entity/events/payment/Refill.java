package com.sobolbetbackend.backendprojektbk1.entity.events.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Refill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Player player;
    private Double replenishmentAmount;
    private LocalDateTime dateOfRefill;
    @ManyToOne
    @JoinColumn(name = "payment_method", referencedColumnName = "name_en")
    private PaymentMethod paymentMethod;
    private Boolean isPaymentSuccessful;

    public Refill() {
    }

    public Refill(Player player, Double replenishmentAmount, LocalDateTime dateOfRefill,
                  PaymentMethod paymentMethod, Boolean isPaymentSuccessful) {
        this.player = player;
        this.replenishmentAmount = replenishmentAmount;
        this.dateOfRefill = dateOfRefill;
        this.paymentMethod = paymentMethod;
        this.isPaymentSuccessful = isPaymentSuccessful;
    }

    public LocalDateTime getDateOfRefill() {
        return dateOfRefill;
    }

    public void setDateOfRefill(LocalDateTime dateOfRefill) {
        this.dateOfRefill = dateOfRefill;
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

    public Double getReplenishmentAmount() {
        return replenishmentAmount;
    }

    public void setReplenishmentAmount(Double replenishmentAmount) {
        this.replenishmentAmount = replenishmentAmount;
    }
}
