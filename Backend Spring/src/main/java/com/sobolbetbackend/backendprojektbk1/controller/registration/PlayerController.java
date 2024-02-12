package com.sobolbetbackend.backendprojektbk1.controller.registration;

import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.exception.EmailAlreadyExistsException;
import com.sobolbetbackend.backendprojektbk1.exception.UserAlreadyRegisteredException;
import com.sobolbetbackend.backendprojektbk1.service.playerRegistrationServices.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<String> registration(@RequestBody UserE user) {
        try {
            playerService.registration(user);
            return ResponseEntity.ok().body("{\"message\": \"You have successfully registered\"}");
        } catch (EmailAlreadyExistsException | UserAlreadyRegisteredException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Exception happened\"}");
        }
    }
}
