package com.sobolbetbackend.backendprojektbk1.controller.uploadFromSportAPI;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.CountryRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.SportRepo;
import com.sobolbetbackend.backendprojektbk1.service.eventSideInfosServices.SportAndCountryService;
import com.sobolbetbackend.backendprojektbk1.service.securityServices.DeveloperKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CountryAndSportUploadController {
    private final String apiCountriesUrl = "https://thesportsdb.com/api/v1/json/123/all_countries.php";
    private final String apiSportsUrl = "https://www.thesportsdb.com/api/v1/json/123/all_sports.php";

    private final DeveloperKeyService developerKeyService;
    private final CountryRepo countryRepo;
    private final SportRepo sportRepo;
    private final SportAndCountryService sportAndCountryService;

    @Autowired
    public CountryAndSportUploadController(DeveloperKeyService developerKeyService, CountryRepo countryRepo, SportRepo sportRepo, SportAndCountryService sportAndCountryService) {
        this.developerKeyService = developerKeyService;
        this.countryRepo = countryRepo;
        this.sportRepo = sportRepo;
        this.sportAndCountryService = sportAndCountryService;
    }

    @PutMapping("/countries/update")
    public ResponseEntity<String> updateCountries(@RequestParam String key) {
        try {
            if (developerKeyService.getDeveloperKey().equals(key)) {
                sportAndCountryService.updateInfo(countryRepo, apiCountriesUrl, "countries","name_en", Country.class);
                return ResponseEntity.ok("Countries updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid developer key");
            }
        } catch (ApiProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }

    @CachePut(cacheNames = {"sportsCache"})
    @PutMapping("/sports/update")
    public ResponseEntity<String> updateSports(@RequestParam String key) {
        try{
            if (developerKeyService.getDeveloperKey().equals(key)) {
                sportAndCountryService.updateInfo(sportRepo, apiSportsUrl, "countries","strSport", Sport.class);
                return ResponseEntity.ok("Sports updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid developer key");
            }
        } catch (ApiProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
    }
}
