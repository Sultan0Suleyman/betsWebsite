package com.sobolbetbackend.backendprojektbk1.controller.bet;

import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList.FullBetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList.OrdinaryBetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.placeBet.FullBetRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.BalanceUpdateService;
import com.sobolbetbackend.backendprojektbk1.exception.LowBalanceException;
import com.sobolbetbackend.backendprojektbk1.service.betServices.FullBetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bet")
public class BetController {
    private final FullBetService fullBetService;
    private final BalanceUpdateService balanceUpdateService;

    @Autowired
    public BetController(FullBetService fullBetService, BalanceUpdateService balanceUpdateService) {
        this.fullBetService = fullBetService;
        this.balanceUpdateService = balanceUpdateService;
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeBet(@RequestBody FullBetRequestDTO fullBetRequestDTO) {
        try {
            fullBetService.placeBet(fullBetRequestDTO);
            balanceUpdateService.balanceWithdraw(fullBetRequestDTO.getUserId(), fullBetRequestDTO.getBetAmount());
            return ResponseEntity.ok().body("{\"message\": \"The bet was made successfully\"}");
        } catch (LowBalanceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Exception happened\"}");
        }

    }

    @GetMapping("/list/fullBet/{userId}")
    public ResponseEntity<List<FullBetDTO>> getAllFullBets(@PathVariable Long userId) {
        return ResponseEntity.ok(fullBetService.getListOfFullBets(userId));
    }

    @GetMapping("/list/ordinaryBets/{fullBetId}")
    public ResponseEntity<List<OrdinaryBetDTO>> getAllOrdinaryBets(@PathVariable Long fullBetId) {
        return ResponseEntity.ok(fullBetService.getListOfOrdinaryBets(fullBetId));
    }

    @GetMapping("/sell-price/{fullBetId}")
    public ResponseEntity<Double> calculateSellPrice(@PathVariable Long fullBetId) {
        try {
            double sellPrice = fullBetService.calculateSellPrice(fullBetId);
            return ResponseEntity.ok(sellPrice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0.0);
        }
    }

    @PutMapping("/sell")
    public ResponseEntity<String> sellBet(@RequestBody Long fullBetId) {
        System.out.println("FullBet"+fullBetId);
        try {
            fullBetService.sellBet(fullBetId);
            return ResponseEntity.ok().body("{\"message\": \"The bet sold successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Bet can't be sold now\"}");
        }
    }
}
