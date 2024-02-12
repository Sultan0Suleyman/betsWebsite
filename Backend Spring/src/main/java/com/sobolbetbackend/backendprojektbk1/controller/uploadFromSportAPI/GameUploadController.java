package com.sobolbetbackend.backendprojektbk1.controller.uploadFromSportAPI;

import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import com.sobolbetbackend.backendprojektbk1.service.mainEventsServices.GamesUpdateService;
import com.sobolbetbackend.backendprojektbk1.service.securityServices.DeveloperKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class GameUploadController {
    private final String apiUrl = "http://127.0.0.1:5000/api/games";
    private final String gameNodeInJsonFile = "games";
    private final String elementOfTheLeagueInJsonFile = "strLeague";
    private final String elementOfTheHomeTeamInJsonFile = "strTeamHome";
    private final String elementOfTheAwayTeamInJsonFile = "strTeamAway";
    private final String elementOfTheCountryInJsonFile = "strCountry";
    private final String elementOfTheSportInJsonFile = "strSport";
    private final String elementOfTheDateInJsonFile = "dateOfMatch";

    private final DeveloperKeyService developerKeyService;
    private final GamesUpdateService gamesUpdateService;

    @Autowired
    public GameUploadController(DeveloperKeyService developerKeyService, GamesUpdateService gamesUpdateService) {
        this.developerKeyService = developerKeyService;
        this.gamesUpdateService = gamesUpdateService;
    }

    @PutMapping("/games/update")
    public ResponseEntity<String> updateGames(@RequestParam String key){
        try{
            if (developerKeyService.getDeveloperKey().equals(key)) {
                gamesUpdateService.updateGames(apiUrl,gameNodeInJsonFile,elementOfTheCountryInJsonFile,
                        elementOfTheSportInJsonFile,elementOfTheLeagueInJsonFile,elementOfTheDateInJsonFile,
                        elementOfTheHomeTeamInJsonFile,elementOfTheAwayTeamInJsonFile);
                return ResponseEntity.ok("Games updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid developer key");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Element not found: " + e.getMessage());
        } catch (ApiProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }
}
