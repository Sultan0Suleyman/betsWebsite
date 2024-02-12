package com.sobolbetbackend.backendprojektbk1.entity.events.payment;

import com.sobolbetbackend.backendprojektbk1.repository.placeBet.FullBetRepo;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceUpdateService {

    private final PlayerRepo playerRepo;
    private final FullBetRepo fullBetRepo;

    @Autowired
    public BalanceUpdateService(PlayerRepo playerRepo, FullBetRepo fullBetRepo) {
        this.playerRepo = playerRepo;
        this.fullBetRepo = fullBetRepo;
    }

    @Transactional
    public void balanceWithdraw(Long userId, Double amount){
        playerRepo.findByUserId(userId)
                .updateBalance(-amount);
    }

    @Transactional
    public void balanceTopUp(Long userId, Double amount){
        playerRepo.findByUserId(userId)
                .updateBalance(amount);
    }

}
