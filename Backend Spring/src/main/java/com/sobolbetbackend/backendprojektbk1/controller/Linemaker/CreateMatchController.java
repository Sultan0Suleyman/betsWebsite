package com.sobolbetbackend.backendprojektbk1.controller.Linemaker;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.CreatedMatchDTO;
import com.sobolbetbackend.backendprojektbk1.service.linemakerServices.CreateMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/linemaker")
public class CreateMatchController {
    private final CreateMatchService createMatchService;

    @Autowired
    public CreateMatchController(CreateMatchService createMatchService) {
        this.createMatchService = createMatchService;
    }

    @GetMapping("/list/countries")
    public ResponseEntity<List<String>> listOfSports() {
        return ResponseEntity.ok(createMatchService.getCountries());
    }

    @GetMapping("/list/leagues/{sport}/{country}")
    public ResponseEntity<List<String>> listOfLeagues(@PathVariable String sport, @PathVariable String country) {
        return ResponseEntity.ok(createMatchService.getLeagues(sport, country));
    }

    @GetMapping("/list/teams/{league}")
    public ResponseEntity<List<String>> listOfTeams(@PathVariable String league) {
        return ResponseEntity.ok(createMatchService.getTeams(league));
    }

    @PostMapping("/create-match")
    public ResponseEntity<?> createMatch(@RequestBody CreatedMatchDTO matchData) {
        try {
            createMatchService.createMatch(matchData);
            return ResponseEntity.ok("Match created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating match: " + e.getMessage());
        }
    }
}
