package com.sobolbetbackend.backendprojektbk1.repository.placeBet;

import com.sobolbetbackend.backendprojektbk1.entity.events.bet.FullBet;
import org.springframework.data.repository.CrudRepository;

public interface FullBetRepo extends CrudRepository<FullBet, Long> {
}
