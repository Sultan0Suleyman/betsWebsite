package com.sobolbetbackend.backendprojektbk1.repository.placeBet;

import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import org.springframework.data.repository.CrudRepository;

public interface OrdinaryBetRepo extends CrudRepository<OrdinaryBet,Long> {
}
