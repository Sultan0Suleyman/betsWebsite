package com.sobolbetbackend.backendprojektbk1.repository.Linemaker;

import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEvent;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingOdd;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BettingOddRepo extends CrudRepository<BettingOdd, Long> {
    List<BettingOdd> findAllByBettingEvent(BettingEvent event);
}
