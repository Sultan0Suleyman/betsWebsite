package com.sobolbetbackend.backendprojektbk1.entity;

import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.FullBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Refill;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Withdraw;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
public class Player{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserE user;
    private Double balance;
    @OneToMany(mappedBy = "player",fetch = FetchType.EAGER)
    private List<FullBet> bets;
    @OneToMany(mappedBy = "player",fetch = FetchType.LAZY)
    private List<Refill> refills;
    @OneToMany(mappedBy = "player",fetch = FetchType.LAZY)
    private List<Withdraw> withdraws;

    public Player() {
        this.balance = (double) 0;
        user = new UserE();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserE getUser() {
        return user;
    }

    public void setUser(UserE user) {
        this.user = user;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public void updateBalance(Double balance){
        this.balance=round(this.balance + balance,2);
    }

    public List<Refill> getRefills() {
        return refills;
    }

    public void setRefills(List<Refill> refills) {
        this.refills = refills;
    }

    public List<Withdraw> getWithdraws() {
        return withdraws;
    }

    public void setWithdraws(List<Withdraw> withdraws) {
        this.withdraws = withdraws;
    }

    public List<FullBet> getBets() {
        return bets;
    }

    public void setBets(List<FullBet> bets) {
        this.bets = bets;
    }
}
