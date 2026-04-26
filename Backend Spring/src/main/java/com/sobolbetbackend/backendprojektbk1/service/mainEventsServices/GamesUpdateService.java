package com.sobolbetbackend.backendprojektbk1.service.mainEventsServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Team;
import com.sobolbetbackend.backendprojektbk1.exception.development.ApiProblemException;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.CountryRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.LeagueRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.SportRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.TeamRepo;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class GamesUpdateService {
    private static final Logger log = LoggerFactory.getLogger(GamesUpdateService.class);

    private final CountryRepo countryRepo;
    private final SportRepo sportRepo;
    private final LeagueRepo leagueRepo;
    private final TeamRepo teamRepo;
    private final GameRepo gameRepo;
    private final RestTemplate restTemplate;

    @Autowired
    public GamesUpdateService(CountryRepo countryRepo, SportRepo sportRepo, LeagueRepo leagueRepo,
                              TeamRepo teamRepo, GameRepo gameRepo, RestTemplate restTemplate) {
        this.countryRepo = countryRepo;
        this.sportRepo = sportRepo;
        this.leagueRepo = leagueRepo;
        this.teamRepo = teamRepo;
        this.gameRepo = gameRepo;
        this.restTemplate = restTemplate;
    }

    public void updateGames(String apiUrl,String node, String countryEl, String sportEl,
                            String leagueEl, String dateOfMatchEl, String teamHomeEl,
                            String teamAwayEl) throws ApiProblemException {
        String games = restTemplate.getForObject(apiUrl, String.class);
        if(games==null|| games.isEmpty()) throw new ApiProblemException("Api reference is invalid");
        List<Game> listOfGames = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(games);

            if (jsonNode.has(node)) {
                JsonNode gamesNode = jsonNode.get(node);

                if (gamesNode.isArray()) {
                    for (JsonNode gameNode : gamesNode) {
                        if (gameNode.has(teamHomeEl)&&gameNode.has(teamAwayEl)) {
                            String strTeamHome = gameNode.get(teamHomeEl).asText();
                            String strTeamAway = gameNode.get(teamAwayEl).asText();
                            String strLeague = gameNode.get(leagueEl).asText();
                            String strCountry = gameNode.get(countryEl).asText();
                            String strSport = gameNode.get(sportEl).asText();

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                            LocalDateTime dateOfMatch = LocalDateTime.parse(gameNode.get(dateOfMatchEl).asText(),
                                    formatter);

                            Country countryEntity = null;

                            if (strCountry != null) {
                                Optional<Country> optionalCountry = countryRepo.findById(strCountry);

                                if (optionalCountry.isPresent()) {
                                    countryEntity = optionalCountry.get();
                                } else {
                                    // Здесь можно предпринять дополнительные действия, если страна не найдена
                                    log.error("Country not found with id: {}", strCountry);
                                }
                            }

                            final Country finalCountryEntity = countryEntity;

                            Sport sportEntity = sportRepo.findById(strSport)
                                    .orElseThrow(() -> new NoSuchElementException(
                                            "Sport not found with id: " + strSport));
                            League leagueEntity = leagueRepo.findById(strLeague).orElseGet(() ->
                                    leagueRepo.save(new League(strLeague, finalCountryEntity, sportEntity))
                            );

                            Team teamHomeEntity = teamRepo.findById(strTeamHome).orElseGet(() ->
                                    teamRepo.save(new Team(strTeamHome, leagueEntity, sportEntity, finalCountryEntity))
                            );

                            Team teamAwayEntity = teamRepo.findById(strTeamAway).orElseGet(() ->
                                    teamRepo.save(new Team(strTeamAway, leagueEntity, sportEntity, finalCountryEntity))
                            );

                            listOfGames.add(new Game(teamHomeEntity,teamAwayEntity,dateOfMatch,
                                    leagueEntity,sportEntity,countryEntity));
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
            throw new ApiProblemException("Error occurred during games update: " + e.getMessage());
        }
        List<Game> existingGames = (List<Game>) gameRepo.findAll();
        List<Game> newGames = listOfGames.stream()
                .filter(game -> existingGames.stream().noneMatch(existingGame -> existingGame.isSameGame(game)))
                .toList();
        gameRepo.saveAll(newGames);
    }
}
