package com.sobolbetbackend.backendprojektbk1.repository.placeBet;

import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrdinaryBetRepo extends CrudRepository<OrdinaryBet,Long> {

    List<OrdinaryBet> findByGameId(Long gameId);
}
