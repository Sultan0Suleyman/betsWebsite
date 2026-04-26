package com.sobolbetbackend.backendprojektbk1.controller.uploadFromSportAPI;

import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import com.sobolbetbackend.backendprojektbk1.service.eventSideInfosServices.LeaguesUpdateService;
import com.sobolbetbackend.backendprojektbk1.service.securityServices.DeveloperKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class LeagueUploadController {
    private final String basicLeaguesApiUrl = "https://www.thesportsdb.com/api/v1/json/123/search_all_leagues.php";
    private final String leagueNodeInJsonFile = "countries";
    private final String elementOfTheLeagueInJsonFile = "strLeague";
    private final String elementOfTheSportInJsonFile = "strSport";

    private final DeveloperKeyService developerKeyService;
    private final LeaguesUpdateService leaguesUpdateService;

    @Autowired
    public LeagueUploadController(DeveloperKeyService developerKeyService,
                                  LeaguesUpdateService leaguesUpdateService) {
        this.developerKeyService = developerKeyService;
        this.leaguesUpdateService = leaguesUpdateService;
    }

    @PutMapping("/leagues/updateByCountry")
    public ResponseEntity<String> updateLeagues(@RequestParam String key, @RequestParam String country) {
        try{
            if (developerKeyService.getDeveloperKey().equals(key)) {
                leaguesUpdateService.updateLeaguesByCountry(basicLeaguesApiUrl,country,leagueNodeInJsonFile,
                        elementOfTheLeagueInJsonFile,elementOfTheSportInJsonFile);
                return ResponseEntity.ok("Leagues for country " + country + " updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid developer key");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Element not found: " + e.getMessage());
        } catch (ApiProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

    @PutMapping("/leagues/updateByCountryAndSport")
    public ResponseEntity<String> updateLeagues(@RequestParam String key, @RequestParam String country,
                                                @RequestParam String sport) {
        try{
            if (developerKeyService.getDeveloperKey().equals(key)) {
                leaguesUpdateService.updateLeaguesByCountryAndSport(basicLeaguesApiUrl,country,sport,
                        leagueNodeInJsonFile, elementOfTheLeagueInJsonFile);
                return ResponseEntity.ok("Leagues for country " + country +
                        " and sport " + sport + " updated successfully");
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
