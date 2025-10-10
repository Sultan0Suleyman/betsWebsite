package com.sobolbetbackend.backendprojektbk1.entity.events.bet;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BettingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;
    // набор коэффициентов (BettingOdd)
    @OneToMany(mappedBy = "bettingEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BettingOdd> odds;

    // статус события: DRAFT, PUBLISHED, CLOSED и т.д.
    @Enumerated(EnumType.STRING)
    private BettingEventStatus status = BettingEventStatus.DRAFT;

}
