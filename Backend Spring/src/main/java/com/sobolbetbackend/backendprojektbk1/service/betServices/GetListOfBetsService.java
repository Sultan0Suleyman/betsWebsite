package com.sobolbetbackend.backendprojektbk1.service.betServices;

import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.BetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.GameBetsResponseDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEvent;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingOdd;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import com.sobolbetbackend.backendprojektbk1.repository.Linemaker.BettingOddRepo;
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
    private final BettingOddRepo bettingOddRepo;

    @Autowired
    public GetListOfBetsService(SportRepo sportRepo, GameRepo gameRepo, CountryRepo countryRepo, LeagueRepo leagueRepo, BettingOddRepo bettingOddRepo) {
        this.sportRepo = sportRepo;
        this.gameRepo = gameRepo;
        this.countryRepo = countryRepo;
        this.leagueRepo = leagueRepo;
        this.bettingOddRepo = bettingOddRepo;
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

    public List<GameBetsResponseDTO> getGames(String sport, String country, String league) {
        Sport sport1 = sportRepo.findById(sport).orElseThrow();
        Country country1 = countryRepo.findById(country).orElse(null);
        League league1 = leagueRepo.findById(league).orElseThrow();

        List<Game> games = (List<Game>) gameRepo.findAllByCountryAndSportAndLeague(country1, sport1, league1);

        System.out.println("REQUEST => sport=" + sport + ", country=" + country + ", league=" + league);
        System.out.println("FOUND GAMES COUNT = " + games.size());

        for (Game game : games) {
            System.out.println("GAME ID = " + game.getId());
            System.out.println("posted = " + game.getGamePosted());
            System.out.println("live = " + game.getGameInLive());
            System.out.println("ended = " + game.getGameEnded());
            System.out.println("bettingEvent = " + (game.getBettingEvent() != null));
            System.out.println("country = " + (game.getCountry() != null ? game.getCountry().getName() : null));
            System.out.println("league = " + (game.getLeague() != null ? game.getLeague().getName() : null));
            System.out.println("-----");
        }

        List<Game> filteredGames = games.stream()
                .filter(game -> game.getGamePosted() && !game.getGameInLive() && !game.getGameEnded())
                .toList();

        List<GameBetsResponseDTO> finalList = new ArrayList<>();

        for (Game game : filteredGames) {
            BettingEvent event = game.getBettingEvent();
            if (event == null) {
                System.out.println("SKIPPED GAME " + game.getId() + " because bettingEvent is null");
                continue;
            }

            List<BettingOdd> odds = bettingOddRepo.findAllByBettingEvent(event);
            System.out.println("GAME " + game.getId() + " odds count = " + odds.size());

            List<BetDTO> bets = new ArrayList<>();
            for (BettingOdd odd : odds) {
                if (odd.getValue() != null) {
                    bets.add(new BetDTO(odd.getType(), odd.getValue()));
                }
            }

            finalList.add(new GameBetsResponseDTO(
                    game.getId(),
                    game.getTeamHome().getName_en(),
                    game.getTeamAway().getName_en(),
                    game.getDateOfMatch(),
                    bets
            ));
        }

        return finalList;
    }


    public GameBetsResponseDTO getGameBets(String gameId) {
        Game game = gameRepo.findById(Long.parseLong(gameId)).orElseThrow();
        BettingEvent event = game.getBettingEvent();
        if (event == null) {
            return new GameBetsResponseDTO(game.getId(),
                    game.getTeamHome().getName_en(),
                    game.getTeamAway().getName_en(),
                    game.getDateOfMatch(),
                    new ArrayList<>());
        }

        List<BettingOdd> odds = bettingOddRepo.findAllByBettingEvent(event);
        List<BetDTO> bets = new ArrayList<>();

        for (BettingOdd odd : odds) {
            if (odd.getValue() != null) {
                bets.add(new BetDTO(odd.getType(), odd.getValue()));
            }
        }

        return new GameBetsResponseDTO(
                game.getId(),
                game.getTeamHome().getName_en(),
                game.getTeamAway().getName_en(),
                game.getDateOfMatch(),
                bets
        );
    }

//    private void addBetIfNotEmpty(List<BetDTO> bets, String type, Double value) {
//        if (value != null) {
//            bets.add(new BetDTO(type, value));
//        }
//    }
}
