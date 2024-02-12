package com.sobolbetbackend.backendprojektbk1.service.paymentServices;

import com.sobolbetbackend.backendprojektbk1.dto.payment.BalanceUpdateRequestDTO;
import com.sobolbetbackend.backendprojektbk1.dto.payment.Refill.RefillRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Refill;
import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import com.sobolbetbackend.backendprojektbk1.repository.payment.PaymentMethodRepo;
import com.sobolbetbackend.backendprojektbk1.repository.payment.RefillRepo;
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
public class RefillService {
    private final PlayerRepo playerRepo;
    private final RefillRepo refillRepo;
    private final PaymentMethodRepo paymentMethodRepo;

    @Autowired
    public RefillService(PlayerRepo playerRepo, RefillRepo refillRepo, PaymentMethodRepo paymentMethodRepo) {
        this.playerRepo = playerRepo;
        this.refillRepo = refillRepo;
        this.paymentMethodRepo = paymentMethodRepo;
    }

    public void declareRefill(RefillRequestDTO refillRequestDTO){
        Player player = playerRepo.findByUserId(refillRequestDTO.getUserId());
        PaymentMethod paymentMethod = paymentMethodRepo.findById(refillRequestDTO.getPaymentMethod()).orElseThrow();
        Date date = new Date();
        // Convert Date to LocalDateTime
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Refill refill = new Refill(player,refillRequestDTO.getAmount(),localDateTime,
                paymentMethod,Boolean.valueOf(refillRequestDTO.getIsPaymentSuccessful()));

        refillRepo.save(refill);
    }

    @Transactional
    public List<Refill> getRefillStory(Long id){
        return new ArrayList<>(playerRepo.findByUserId(id).getRefills());
    }
}
