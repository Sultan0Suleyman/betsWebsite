package com.sobolbetbackend.backendprojektbk1.controller.payment;

import com.sobolbetbackend.backendprojektbk1.dto.payment.BalanceUpdateRequestDTO;
import com.sobolbetbackend.backendprojektbk1.dto.payment.Refill.RefillRequestDTO;
import com.sobolbetbackend.backendprojektbk1.dto.payment.Refill.RefillResponseDTO;
import com.sobolbetbackend.backendprojektbk1.dto.payment.Withdraw.WithdrawRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.BalanceUpdateService;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Refill;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Withdraw;
import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import com.sobolbetbackend.backendprojektbk1.exception.LowBalanceException;
import com.sobolbetbackend.backendprojektbk1.repository.payment.PaymentMethodRepo;
import com.sobolbetbackend.backendprojektbk1.service.paymentServices.RefillService;
import com.sobolbetbackend.backendprojektbk1.service.paymentServices.WithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentMethodRepo paymentMethodRepo;
    private final RefillService refillService;
    private final WithdrawService withdrawService;
    private final BalanceUpdateService balanceUpdateService;

    @Autowired
    public PaymentController(PaymentMethodRepo paymentMethodRepo, RefillService refillService, WithdrawService withdrawService, BalanceUpdateService balanceUpdateService) {
        this.paymentMethodRepo = paymentMethodRepo;
        this.refillService = refillService;
        this.withdrawService = withdrawService;
        this.balanceUpdateService = balanceUpdateService;
    }

    @GetMapping("/methods")
    public ResponseEntity<List<String>> getPaymentMethods() {
        List<String> list = new ArrayList<>();
        for (PaymentMethod paymentMethod : paymentMethodRepo.findAll()) {
            list.add(paymentMethod.getName_en());
        }
        return ResponseEntity.ok(list);
    }


    @PostMapping("/refill/add")
    public ResponseEntity<?> addRefillToStory(@RequestBody RefillRequestDTO refillRequestDTO) {
        try{
            refillService.declareRefill(refillRequestDTO);
            return ResponseEntity.ok(new RefillResponseDTO(Boolean.valueOf(refillRequestDTO.getIsPaymentSuccessful()),
                    refillRequestDTO.getAmount()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw/add")
    public ResponseEntity<String> addWithdrawToStory(@RequestBody WithdrawRequestDTO withdrawRequestDTO) {
        try{
            withdrawService.declareWithdraw(withdrawRequestDTO);
            balanceUpdateService.balanceWithdraw(withdrawRequestDTO.getUserId(),withdrawRequestDTO.getAmount());
            return ResponseEntity.ok().body("{\"message\": \"Withdrawal request added successfully\"}");
        }catch(LowBalanceException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Exception happened\"}");
        }
    }

    @GetMapping("/refill/story/{userId}")
    public ResponseEntity<List<Refill>> getRefillStory(@PathVariable String userId){
        return ResponseEntity.ok(refillService.getRefillStory(Long.parseLong(userId)));
    }

    @GetMapping("/withdraw/story/{userId}")
    public ResponseEntity<List<Withdraw>> getWithdrawStory(@PathVariable String userId){
        return ResponseEntity.ok(withdrawService.getWithdrawStory(Long.parseLong(userId)));
    }

    @PatchMapping("/updateBalance")
    public void updateBalance(@RequestBody BalanceUpdateRequestDTO balanceUpdateRequestDTO){
        balanceUpdateService.balanceTopUp(balanceUpdateRequestDTO.getUserId(),
                balanceUpdateRequestDTO.getAmount());
    }

}