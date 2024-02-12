package com.sobolbetbackend.backendprojektbk1.service.eventSideInfosServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Team;
import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.CountryRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.LeagueRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.SportRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.TeamRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TeamsUpdateService {
    private static final Logger log = LoggerFactory.getLogger(TeamsUpdateService.class);

    private final RestTemplate restTemplate;
    private final TeamRepo teamRepo;
    private final CountryRepo countryRepo;
    private final SportRepo sportRepo;
    private final LeagueRepo leagueRepo;

    @Autowired
    public TeamsUpdateService(RestTemplate restTemplate, TeamRepo teamRepo, CountryRepo countryRepo, SportRepo sportRepo, LeagueRepo leagueRepo) {
        this.restTemplate = restTemplate;
        this.teamRepo = teamRepo;
        this.countryRepo = countryRepo;
        this.sportRepo = sportRepo;
        this.leagueRepo = leagueRepo;
    }

    public void updateTeams(String apiUrlBasic, String country, String sport,
                            String node, String leagueEl, String teamEl) throws ApiProblemException {
        Country countryEntity = countryRepo.findById(country)
                .orElseThrow(() -> new NoSuchElementException("Country not found with id: " + country));
        Sport sportEntity = sportRepo.findById(sport)
                .orElseThrow(() -> new NoSuchElementException("Sport not found with id: " + sport));
        String apiUrl = apiUrlBasic + "?c=" + country + "&s=" + sport;
        String teams = restTemplate.getForObject(apiUrl, String.class);
        if(teams==null|| teams.isEmpty()) throw new ApiProblemException("Api reference is invalid");
        List<Team> listOfTeams = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(teams);

            if (jsonNode.has(node)) {
                JsonNode teamsNode = jsonNode.get(node);

                if (teamsNode.isArray()) {
                    for (JsonNode teamNode : teamsNode) {
                        if (teamNode.has(teamEl)) {
                            String strTeam = teamNode.get(teamEl).asText();
                            String strLeague = teamNode.get(leagueEl).asText();

                            League leagueEntity = leagueRepo.findById(strLeague)
                                    .orElseThrow(() -> new NoSuchElementException(
                                            "League not found with id: " + strLeague));

                            listOfTeams.add(new Team(strTeam,leagueEntity,sportEntity,countryEntity));
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
            throw new ApiProblemException("Error occurred during teams update: " + e.getMessage());
        }
        List<Team> existingTeams = (List<Team>) teamRepo.findAll();
        List<Team> newTeams = listOfTeams.stream()
                .filter(team -> !existingTeams.contains(team))
                .toList();
        teamRepo.saveAll(newTeams);
    }
}
