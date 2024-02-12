package com.sobolbetbackend.backendprojektbk1.service.betServices;

import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.BetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.GameBetsResponseDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.CountryRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.LeagueRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.SportRepo;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetListOfBetsService {
    private final SportRepo sportRepo;
    private final GameRepo gameRepo;
    private final CountryRepo countryRepo;
    private final LeagueRepo leagueRepo;

    @Autowired
    public GetListOfBetsService(SportRepo sportRepo, GameRepo gameRepo, CountryRepo countryRepo, LeagueRepo leagueRepo) {
        this.sportRepo = sportRepo;
        this.gameRepo = gameRepo;
        this.countryRepo = countryRepo;
        this.leagueRepo = leagueRepo;
    }

    @Cacheable(cacheNames = {"sportsCache"})
    public List<String> getSports() {
        List<Sport> sports = (List<Sport>) sportRepo.findAll();
        List<String> list = new ArrayList<>();
        for(Sport sport:sports){
            list.add(sport.getName_en());
        }
        return list;
    }

    @Cacheable(cacheNames = {"countriesCache"}, key = "#sport")
    public List<String> getCountries(String sport){
        Sport sport1 = sportRepo.findById(sport).orElseThrow();
        List<Game> games = (List<Game>) gameRepo.findAllBySport(sport1);

        return games.stream()
                .filter(Game::getGamePosted) // Фильтруем только игры, которые были опубликованы
                .map(game -> game.getCountry() != null ? game.getCountry().getName() : game.getLeague().getName())
                .distinct()
                .toList();
    }

    @Cacheable(cacheNames = {"leaguesCache"}, key = "#sport + '|' + #country")
    public List<String> getLeagues(String sport, String country){
        Sport sport1 = sportRepo.findById(sport).orElseThrow();
        Country country1 = countryRepo.findById(country).orElse(null);
        if (country1 == null) {
            return null;
        }
        List<Game> games = (List<Game>) gameRepo.findAllByCountryAndSport(country1,sport1);

        return games.stream()
                .filter(Game::getGamePosted) // Фильтруем только игры, которые были опубликованы
                .map(game -> game.getLeague().getName())
                .distinct()
                .toList();
    }

    @Cacheable(cacheNames = {"gamesCache"}, key = "#sport + '|' + #country + '|' + #league")
    public List<GameBetsResponseDTO> getGames(String sport, String country, String league){
        Sport sport1 = sportRepo.findById(sport).orElseThrow();
        Country country1 = countryRepo.findById(country).orElse(null);
        League league1 = leagueRepo.findById(league).orElseThrow();

        List<Game> games = (List<Game>) gameRepo.findAllByCountryAndSportAndLeague(country1,sport1,league1);

        List<Game> filteredGames = games.stream()
                .filter(game -> game.getGamePosted() && !game.getGameInLive() && !game.getGameEnded())
                .toList();

        List<GameBetsResponseDTO> finalList = new ArrayList<>();

        for(Game game: filteredGames){
            List<BetDTO> bets = new ArrayList<>();

            // Добавлять ставки в список только если они не пустые
            addBetIfNotEmpty(bets, "Win1", game.getBettingEvent().getWin1());
            addBetIfNotEmpty(bets, "Draw", game.getBettingEvent().getDraw());
            addBetIfNotEmpty(bets, "Win2", game.getBettingEvent().getWin2());
            addBetIfNotEmpty(bets, "Win of 1 team in match", game.getBettingEvent().getW1InMatch());
            addBetIfNotEmpty(bets, "Win of 2 team in match", game.getBettingEvent().getW2InMatch());

            finalList.add(new GameBetsResponseDTO(game.getId(),
                    game.getTeamHome().getName_en(),
                    game.getTeamAway().getName_en(),
                    game.getDateOfMatch(),
                    bets));
        }
        return finalList;
    }
    public GameBetsResponseDTO getGameBets(String gameId){
        Game game = gameRepo.findById(Long.parseLong(gameId)).orElseThrow();

        List<BetDTO> bets = new ArrayList<>();

        // Добавлять ставки в список только если они не пустые
        addBetIfNotEmpty(bets, "Win1", game.getBettingEvent().getWin1());
        addBetIfNotEmpty(bets, "Draw", game.getBettingEvent().getDraw());
        addBetIfNotEmpty(bets, "Win2", game.getBettingEvent().getWin2());
        addBetIfNotEmpty(bets, "1X", game.getBettingEvent().getX1());
        addBetIfNotEmpty(bets, "12", game.getBettingEvent().getW12());
        addBetIfNotEmpty(bets, "2X", game.getBettingEvent().getX2());
        addBetIfNotEmpty(bets, "Win of 1 team in match", game.getBettingEvent().getW1InMatch());
        addBetIfNotEmpty(bets, "Win of 2 team in match", game.getBettingEvent().getW2InMatch());
        addBetIfNotEmpty(bets, "First team scores first", game.getBettingEvent().getFirstTeamScoresFirst());
        addBetIfNotEmpty(bets, "Second team scores first", game.getBettingEvent().getSecondTeamScoresFirst());

        return new GameBetsResponseDTO(
                game.getId(),
                game.getTeamHome().getName_en(),
                game.getTeamAway().getName_en(),
                game.getDateOfMatch(),
                bets
        );
    }

    private void addBetIfNotEmpty(List<BetDTO> bets, String type, Double value) {
        if (value != null) {
            bets.add(new BetDTO(type, value));
        }
    }
}
