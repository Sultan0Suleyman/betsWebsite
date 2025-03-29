package com.sobolbetbackend.backendprojektbk1.controller.registration;

import com.sobolbetbackend.backendprojektbk1.controller.authorisation.AuthController;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.exception.EmailAlreadyExistsException;
import com.sobolbetbackend.backendprojektbk1.exception.UserAlreadyRegisteredException;
import com.sobolbetbackend.backendprojektbk1.service.playerRegistrationServices.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/players")
public class PlayerController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
            log.error("Authentication error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Exception occurred during registration", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Exception happened\"}");
        }
    }
}
