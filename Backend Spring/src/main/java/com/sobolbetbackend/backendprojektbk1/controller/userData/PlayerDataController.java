package com.sobolbetbackend.backendprojektbk1.controller.userData;

import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player/data")
public class PlayerDataController {

    private final PlayerRepo playerRepo;

    @Autowired
    public PlayerDataController(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }

    @GetMapping("/balance/{playerId}")
    public ResponseEntity<Double> getPlayerBalance(@PathVariable String playerId){
        Double balance = playerRepo.findByUserId(Long.valueOf(playerId)).getBalance();

        return ResponseEntity.ok(balance);
    }
}
