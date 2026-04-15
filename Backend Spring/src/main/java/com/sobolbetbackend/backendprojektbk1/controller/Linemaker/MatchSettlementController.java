package com.sobolbetbackend.backendprojektbk1.controller.Linemaker;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.BetCalculationResultDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.RefundResultDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsSettlement.FinishGameRequestDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsSettlement.SettlementMatchDTO;
import com.sobolbetbackend.backendprojektbk1.service.matchSettlementServices.MatchSettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/linemaker/settlement")
public class MatchSettlementController {

    private final MatchSettlementService matchSettlementService;

    public MatchSettlementController(MatchSettlementService matchSettlementService) {
        this.matchSettlementService = matchSettlementService;
    }

    @GetMapping("/games")
    public ResponseEntity<List<SettlementMatchDTO>> getSettlementMatches() {
        return ResponseEntity.ok(matchSettlementService.getMatchesForSettlement());
    }

    @GetMapping("/games/{gameId}")
    public ResponseEntity<SettlementMatchDTO> getSettlementMatch(@PathVariable Long gameId) {
        return ResponseEntity.ok(matchSettlementService.getSettlementMatch(gameId));
    }

    @PostMapping("/games/{gameId}/finish")
    public ResponseEntity<BetCalculationResultDTO> finishGame(
            @PathVariable Long gameId,
            @RequestBody FinishGameRequestDTO request
    ) {
        return ResponseEntity.ok(matchSettlementService.finishGameAndSettle(gameId, request));
    }

    @PostMapping("/games/{gameId}/cancel")
    public ResponseEntity<RefundResultDTO> cancelGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(matchSettlementService.cancelGameAndRefund(gameId));
    }

    @PostMapping("/games/{gameId}/save-score")
    public ResponseEntity<SettlementMatchDTO> saveScore(
            @PathVariable Long gameId,
            @RequestBody FinishGameRequestDTO request
    ) {
        return ResponseEntity.ok(matchSettlementService.saveScore(gameId, request));
    }
}
