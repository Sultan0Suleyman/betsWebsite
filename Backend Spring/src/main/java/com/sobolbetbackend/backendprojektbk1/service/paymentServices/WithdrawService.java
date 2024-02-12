package com.sobolbetbackend.backendprojektbk1.service.paymentServices;

import com.sobolbetbackend.backendprojektbk1.dto.payment.Withdraw.WithdrawRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Withdraw;
import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import com.sobolbetbackend.backendprojektbk1.exception.LowBalanceException;
import com.sobolbetbackend.backendprojektbk1.repository.payment.PaymentMethodRepo;
import com.sobolbetbackend.backendprojektbk1.repository.payment.WithdrawRepo;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WithdrawService {
    private final PlayerRepo playerRepo;
    private final WithdrawRepo withdrawRepo;
    private final PaymentMethodRepo paymentMethodRepo;

    @Autowired
    public WithdrawService(PlayerRepo playerRepo, WithdrawRepo withdrawRepo, PaymentMethodRepo paymentMethodRepo) {
        this.playerRepo = playerRepo;
        this.withdrawRepo = withdrawRepo;
        this.paymentMethodRepo = paymentMethodRepo;
    }

    public void declareWithdraw(WithdrawRequestDTO withdrawRequestDTO) throws LowBalanceException {
        Player player = playerRepo.findByUserId(withdrawRequestDTO.getUserId());
        if(player.getBalance()<withdrawRequestDTO.getAmount()){
            throw new LowBalanceException("Insufficient funds in the account");
        }else {
            PaymentMethod paymentMethod = paymentMethodRepo.findById(withdrawRequestDTO.getPaymentMethod()).orElseThrow();
            Date date = new Date();
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Withdraw withdraw = new Withdraw(player, withdrawRequestDTO.getAmount(), localDateTime,
                    paymentMethod, withdrawRequestDTO.getAccountNumber());

            withdrawRepo.save(withdraw);
        }
    }

    @Transactional
    public List<Withdraw> getWithdrawStory(Long id){
        return new ArrayList<>(playerRepo.findByUserId(id).getWithdraws());
    }
}
