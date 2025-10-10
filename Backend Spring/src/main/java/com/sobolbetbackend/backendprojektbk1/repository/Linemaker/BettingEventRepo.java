package com.sobolbetbackend.backendprojektbk1.repository.Linemaker;

import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEvent;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BettingEventRepo extends CrudRepository<BettingEvent, Long> {
    Optional<BettingEvent> findByGame(Game game);
}
