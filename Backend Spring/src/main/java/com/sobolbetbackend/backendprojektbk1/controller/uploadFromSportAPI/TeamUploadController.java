package com.sobolbetbackend.backendprojektbk1.controller.uploadFromSportAPI;

import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import com.sobolbetbackend.backendprojektbk1.service.eventSideInfosServices.TeamsUpdateService;
import com.sobolbetbackend.backendprojektbk1.service.securityServices.DeveloperKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class TeamUploadController {
    private final String basicLeaguesApiUrl = "https://www.thesportsdb.com/api/v1/json/123/search_all_teams.php";

    private final String teamNodeInJsonFile = "teams";
    private final String elementOfTheLeagueInJsonFile = "strLeague";
    private final String elementOfTheTeamInJsonFile = "strTeam";

    private final DeveloperKeyService developerKeyService;
    private final TeamsUpdateService teamsUpdateService;

    @Autowired
    public TeamUploadController(DeveloperKeyService developerKeyService, TeamsUpdateService teamsUpdateService) {
        this.developerKeyService = developerKeyService;
        this.teamsUpdateService = teamsUpdateService;
    }

    @PutMapping("/teams/update")
    public ResponseEntity<String> updateTeams(@RequestParam String key, @RequestParam String country,
                                              @RequestParam String sport) {
        try{
            if (developerKeyService.getDeveloperKey().equals(key)) {
                teamsUpdateService.updateTeams(basicLeaguesApiUrl,country,sport,
                        teamNodeInJsonFile,elementOfTheLeagueInJsonFile,elementOfTheTeamInJsonFile);
                return ResponseEntity.ok("Teams for country " + country +
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
