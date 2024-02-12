package com.sobolbetbackend.backendprojektbk1.service.eventSideInfosServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.CountryRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.LeagueRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.SportRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LeaguesUpdateService {
    private static final Logger log = LoggerFactory.getLogger(LeaguesUpdateService.class);

    private final RestTemplate restTemplate;
    private final LeagueRepo leagueRepo;
    private final CountryRepo countryRepo;
    private final SportRepo sportRepo;

    @Autowired
    public LeaguesUpdateService(RestTemplate restTemplate, LeagueRepo leagueRepo,
                                CountryRepo countryRepo, SportRepo sportRepo) {
        this.restTemplate = restTemplate;
        this.leagueRepo = leagueRepo;
        this.countryRepo = countryRepo;
        this.sportRepo = sportRepo;
    }

    public void updateLeaguesByCountry(String apiUrlBasic, String country,
                                       String node, String leagueEl, String sportEl) throws ApiProblemException {
        Country countryEntity = countryRepo.findById(country)
                .orElseThrow(() -> new NoSuchElementException("Country not found with id: " + country));
        String apiUrl = apiUrlBasic + "?c=" + country;
        String leagues = restTemplate.getForObject(apiUrl, String.class);
        if(leagues==null|| leagues.isEmpty()) throw new ApiProblemException("Api reference is invalid");
        List<League> listOfLeagues = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(leagues);

            if (jsonNode.has(node)) {
                JsonNode leaguesNode = jsonNode.get(node);

                if (leaguesNode.isArray()) {
                    for (JsonNode leagueNode : leaguesNode) {
                        if (leagueNode.has(leagueEl)&&leagueNode.has(sportEl)) {
                            String strSport = leagueNode.get(sportEl).asText();
                            String strLeague = leagueNode.get(leagueEl).asText();

                            Sport sportEntity = sportRepo.findById(strSport)
                                    .orElseThrow(() -> new NoSuchElementException(
                                            "Sport not found with id: " + strSport));

                            listOfLeagues.add(new League(strLeague, countryEntity, sportEntity));
                        } else{
                            throw new ApiProblemException("Api reference problems. Problems with elements of Node");
                        }
                    }
                } else{
                    throw new ApiProblemException("Api reference problems. Node is not array");
                }
            }else{
                throw new ApiProblemException("Api reference problems. Node doesn't exist");
            }
        } catch (Exception e) {
            log.error("Exception happened: {}", e.getMessage());
            throw new ApiProblemException("Error occurred during leagues update" + e.getMessage());
        }
        List<League> existingLeagues = (List<League>) leagueRepo.findAll();
        List<League> newLeagues = listOfLeagues.stream()
                .filter(league -> !existingLeagues.contains(league))
                .toList();
        leagueRepo.saveAll(newLeagues);
    }
    public void updateLeaguesByCountryAndSport(String apiUrlBasic, String country, String sport,
                                               String node, String leagueEl) throws ApiProblemException {
        Country countryEntity = countryRepo.findById(country)
                .orElseThrow(() -> new NoSuchElementException("Country not found with id: " + country));

        Sport sportEntity = sportRepo.findById(sport)
                .orElseThrow(() -> new NoSuchElementException("Sport not found with id: " + sport));
        String apiUrl = apiUrlBasic + "?c=" + country + "&s=" + sport;
        String leagues = restTemplate.getForObject(apiUrl, String.class);
        if(leagues==null|| leagues.isEmpty()) throw new ApiProblemException("Api reference is invalid");
        List<League> listOfLeagues = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(leagues);

            if (jsonNode.has(node)) {
                JsonNode leaguesNode = jsonNode.get(node);

                if (leaguesNode.isArray()) {
                    for (JsonNode leagueNode : leaguesNode) {
                        if (leagueNode.has(leagueEl)) {
                            String strLeague = leagueNode.get(leagueEl).asText();

                            listOfLeagues.add(new League(strLeague, countryEntity, sportEntity));
                        } else{
                            throw new ApiProblemException("Api reference problems. Problems with elements of Node");
                        }
                    }
                } else{
                    throw new ApiProblemException("Api reference problems. Node is not array");
                }
            }else{
                throw new ApiProblemException("Api reference problems. Node doesn't exist");
            }
        } catch (Exception e) {
            log.error("Exception happened: {}", e.getMessage());
            throw new ApiProblemException("Error occurred during leagues update: " + e.getMessage());
        }
        List<League> existingLeagues = (List<League>) leagueRepo.findAll();
        List<League> newLeagues = listOfLeagues.stream()
                .filter(league -> !existingLeagues.contains(league))
                .toList();
        leagueRepo.saveAll(newLeagues);
    }
}
