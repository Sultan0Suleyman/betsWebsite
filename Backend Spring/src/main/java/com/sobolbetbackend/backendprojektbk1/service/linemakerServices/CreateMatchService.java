package com.sobolbetbackend.backendprojektbk1.service.linemakerServices;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.CreatedMatchDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.*;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.CountryRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.LeagueRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.SportRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.TeamRepo;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing match creation logic,
 * including fetching countries, leagues, teams, and saving new matches.
 */
@Service
public class CreateMatchService {
    private final CountryRepo countryRepo;
    private final SportRepo sportRepo;
    private final LeagueRepo leagueRepo;
    private final TeamRepo teamRepo;
    private final GameRepo gameRepo;

    @Autowired
    public CreateMatchService(CountryRepo countryRepo, SportRepo sportRepo, LeagueRepo leagueRepo, TeamRepo teamRepo, GameRepo gameRepo) {
        this.countryRepo = countryRepo;
        this.sportRepo = sportRepo;
        this.leagueRepo = leagueRepo;
        this.teamRepo = teamRepo;
        this.gameRepo = gameRepo;
    }

    /**
     * Retrieves a list of all country names from the database.
     *
     * @return list of country names
     */
    public List<String> getCountries() {
        List<Country> countries = (List<Country>) countryRepo.findAll();
        List<String> list = new ArrayList<>();
        for (Country country : countries) {
            list.add(country.getName());
        }
        return list;
    }

    /**
     * Retrieves leagues by sport and optionally by country.
     * If country is null or blank, all leagues for the sport are returned.
     *
     * @param sport   the sport name
     * @param country the country name (can be null or blank)
     * @return list of league names
     */
    public List<String> getLeagues(String sport, String country) {
        Sport sport1 = sportRepo.findById(sport).orElseThrow();
        List<League> leagues;

        if (country == null || country.isBlank()) {
            // If no country is provided — fetch all leagues for the sport
            leagues = leagueRepo.findBySport(sport1);
        } else {
            Country country1 = countryRepo.findById(country).orElse(null);
            leagues = leagueRepo.findBySportAndCountry(sport1, country1);
        }

        return leagues.stream()
                .map(League::getName)
                .toList();
    }

    /**
     * Retrieves all teams that belong to a given league.
     *
     * @param league the league name
     * @return list of team names
     */
    public List<String> getTeams(String league) {
        League league1 = leagueRepo.findById(league).orElseThrow();
        List<Team> teams = teamRepo.findByLeague(league1);
        List<String> list = new ArrayList<>();
        for (Team team : teams) {
            list.add(team.getName_en());
        }
        return list;
    }

    /**
     * Creates a new match in the system.
     * Steps:
     * - Parses date and time from the DTO
     * - Fetches related Sport, League, Country, and Teams from the database
     * - Validates that a match with the same teams and date does not already exist
     * - Persists the new Game entity
     *
     * @param matchDTO the data transfer object containing match creation details
     * @throws IllegalArgumentException if any entity is not found or if a duplicate match exists
     */
    @Transactional
    public void createMatch(CreatedMatchDTO matchDTO) {
        // Parse date and time
        LocalDateTime matchDateTime = LocalDateTime.parse(matchDTO.getDateOfMatch());

        // Retrieve entities from the database
        Sport sport = sportRepo.findById(matchDTO.getSport())
                .orElseThrow(() -> new IllegalArgumentException("Sport not found: " + matchDTO.getSport()));

        League league = leagueRepo.findById(matchDTO.getLeague())
                .orElseThrow(() -> new IllegalArgumentException("League not found: " + matchDTO.getLeague()));

        Team teamHome = teamRepo.findById(matchDTO.getTeamHome())
                .orElseThrow(() -> new IllegalArgumentException("Home team not found: " + matchDTO.getTeamHome()));

        Team teamAway = teamRepo.findById(matchDTO.getTeamAway())
                .orElseThrow(() -> new IllegalArgumentException("Away team not found: " + matchDTO.getTeamAway()));

        // Handle country (may be null)
        Country country = null;
        if (matchDTO.getCountry() != null && !matchDTO.getCountry().isBlank()) {
            country = countryRepo.findById(matchDTO.getCountry())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found: " + matchDTO.getCountry()));
        }

        // Check for duplicate match
        List<Game> existingGames = gameRepo.findByTeamHomeAndTeamAwayAndDateOfMatch(
                teamHome, teamAway, matchDateTime);

        if (!existingGames.isEmpty()) {
            throw new IllegalArgumentException("Match already exists between these teams on this date");
        }

        // Create new Game entity
        Game newGame = new Game(teamHome, teamAway, matchDateTime, league, sport, country);

        // Save to database
        gameRepo.save(newGame);
    }
}
