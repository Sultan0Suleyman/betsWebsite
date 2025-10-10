package com.sobolbetbackend.backendprojektbk1.entity.events.bet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "betting_odd")
public class BettingOdd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private BettingEvent bettingEvent;

    @Column(nullable = false)
    private String type; // например "WIN1", "DRAW", "TOTAL_OVER_2_5"

    private Double value;

    public BettingOdd(BettingEvent event, String type, Double value) {
        this.bettingEvent = event;
        this.type = type;
        this.value = value;
    }
}
