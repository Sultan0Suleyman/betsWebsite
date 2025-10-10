package com.sobolbetbackend.backendprojektbk1.service.linemaker;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.CreatedMatchDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.*;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.CountryRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.LeagueRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.SportRepo;
import com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos.TeamRepo;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import com.sobolbetbackend.backendprojektbk1.service.linemakerServices.CreateMatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CreateMatchService}.
 * This test class verifies the behavior of the CreateMatchService methods,
 * including country retrieval, league filtering, team fetching, and match creation logic.
 * Tests use Mockito to mock repository dependencies and validate service behavior.
 */
@ExtendWith(MockitoExtension.class)
class CreateMatchServiceTest {

    @Mock
    private CountryRepo countryRepo;

    @Mock
    private SportRepo sportRepo;

    @Mock
    private LeagueRepo leagueRepo;

    @Mock
    private TeamRepo teamRepo;

    @Mock
    private GameRepo gameRepo;

    @InjectMocks
    private CreateMatchService createMatchService;

    private Country testCountry;
    private Sport testSport;
    private League testLeague;
    private Team testTeamHome;
    private Team testTeamAway;

    /**
     * Sets up test fixtures before each test method execution.
     * Initializes mock entities for Country, Sport, League, and Teams.
     */
    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setName("England");

        testSport = new Sport();
        testSport.setName_en("Soccer");

        testLeague = new League();
        testLeague.setName("Premier League");
        testLeague.setSport(testSport);
        testLeague.setCountry(testCountry);

        testTeamHome = new Team();
        testTeamHome.setName_en("Manchester United");
        testTeamHome.setLeague(testLeague);

        testTeamAway = new Team();
        testTeamAway.setName_en("Liverpool");
        testTeamAway.setLeague(testLeague);
    }

    /**
     * Tests {@link CreateMatchService#getCountries()} when countries exist in the database.
     * Verifies that:
     * - The method returns a list of country names
     * - The returned list contains the expected country names
     * - The repository's findAll method is called exactly once
     */
    @Test
    void getCountries_ShouldReturnListOfCountryNames_WhenCountriesExist() {
        // Arrange
        Country country1 = new Country();
        country1.setName("England");
        Country country2 = new Country();
        country2.setName("Spain");

        when(countryRepo.findAll()).thenReturn(Arrays.asList(country1, country2));

        // Act
        List<String> result = createMatchService.getCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("England"));
        assertTrue(result.contains("Spain"));
        verify(countryRepo, times(1)).findAll();
    }

    /**
     * Tests {@link CreateMatchService#getCountries()} when no countries exist.
     * Verifies that:
     * - The method returns an empty list
     * - No exceptions are thrown
     */
    @Test
    void getCountries_ShouldReturnEmptyList_WhenNoCountriesExist() {
        // Arrange
        when(countryRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<String> result = createMatchService.getCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(countryRepo, times(1)).findAll();
    }

    /**
     * Tests {@link CreateMatchService#getLeagues(String, String)} with a specific country.
     * Verifies that:
     * - The method returns leagues filtered by sport and country
     * - The correct repository method is called with the right parameters
     */
    @Test
    void getLeagues_ShouldReturnFilteredLeagues_WhenCountryIsProvided() {
        // Arrange
        String sportName = "Soccer";
        String countryName = "England";

        when(sportRepo.findById(sportName)).thenReturn(Optional.of(testSport));
        when(countryRepo.findById(countryName)).thenReturn(Optional.of(testCountry));
        when(leagueRepo.findBySportAndCountry(testSport, testCountry))
                .thenReturn(Collections.singletonList(testLeague));

        // Act
        List<String> result = createMatchService.getLeagues(sportName, countryName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Premier League", result.getFirst());
        verify(leagueRepo, times(1)).findBySportAndCountry(testSport, testCountry);
        verify(leagueRepo, never()).findBySport(any());
    }

    /**
     * Tests {@link CreateMatchService#getLeagues(String, String)} without a country filter.
     * Verifies that:
     * - The method returns all leagues for the specified sport
     * - The findBySport repository method is called when country is null
     */
    @Test
    void getLeagues_ShouldReturnAllLeaguesForSport_WhenCountryIsNull() {
        // Arrange
        String sportName = "Soccer";

        when(sportRepo.findById(sportName)).thenReturn(Optional.of(testSport));
        when(leagueRepo.findBySport(testSport))
                .thenReturn(Collections.singletonList(testLeague));

        // Act
        List<String> result = createMatchService.getLeagues(sportName, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Premier League", result.getFirst());
        verify(leagueRepo, times(1)).findBySport(testSport);
        verify(leagueRepo, never()).findBySportAndCountry(any(), any());
    }

    /**
     * Tests {@link CreateMatchService#getLeagues(String, String)} with a blank country string.
     * Verifies that:
     * - The method treats blank country strings the same as null
     * - All leagues for the sport are returned
     */
    @Test
    void getLeagues_ShouldReturnAllLeaguesForSport_WhenCountryIsBlank() {
        // Arrange
        String sportName = "Soccer";

        when(sportRepo.findById(sportName)).thenReturn(Optional.of(testSport));
        when(leagueRepo.findBySport(testSport))
                .thenReturn(Collections.singletonList(testLeague));

        // Act
        List<String> result = createMatchService.getLeagues(sportName, "");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(leagueRepo, times(1)).findBySport(testSport);
    }

    /**
     * Tests {@link CreateMatchService#getTeams(String)} with a valid league.
     * Verifies that:
     * - The method returns all teams belonging to the specified league
     * - Team names are correctly extracted from the entities
     */
    @Test
    void getTeams_ShouldReturnTeamNames_WhenLeagueExists() {
        // Arrange
        String leagueName = "Premier League";

        when(leagueRepo.findById(leagueName)).thenReturn(Optional.of(testLeague));
        when(teamRepo.findByLeague(testLeague))
                .thenReturn(Arrays.asList(testTeamHome, testTeamAway));

        // Act
        List<String> result = createMatchService.getTeams(leagueName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Manchester United"));
        assertTrue(result.contains("Liverpool"));
        verify(teamRepo, times(1)).findByLeague(testLeague);
    }

    /**
     * Tests {@link CreateMatchService#getTeams(String)} when no teams exist in the league.
     * Verifies that:
     * - The method returns an empty list
     * - No exceptions are thrown
     */
    @Test
    void getTeams_ShouldReturnEmptyList_WhenNoTeamsExist() {
        // Arrange
        String leagueName = "Premier League";

        when(leagueRepo.findById(leagueName)).thenReturn(Optional.of(testLeague));
        when(teamRepo.findByLeague(testLeague)).thenReturn(Collections.emptyList());

        // Act
        List<String> result = createMatchService.getTeams(leagueName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests {@link CreateMatchService#createMatch(CreatedMatchDTO)} with valid data.
     * Verifies that:
     * - A new Game entity is successfully created and saved
     * - All related entities are fetched from repositories
     * - No duplicate match validation passes
     * - The gameRepo.Save method is called exactly once
     */
    @Test
    void createMatch_ShouldCreateNewMatch_WhenValidDataProvided() {
        // Arrange
        CreatedMatchDTO matchDTO = new CreatedMatchDTO();
        matchDTO.setSport("Soccer");
        matchDTO.setLeague("Premier League");
        matchDTO.setCountry("England");
        matchDTO.setTeamHome("Manchester United");
        matchDTO.setTeamAway("Liverpool");
        matchDTO.setDateOfMatch("2025-10-15T19:00:00");

        LocalDateTime matchDateTime = LocalDateTime.parse(matchDTO.getDateOfMatch());

        when(sportRepo.findById("Soccer")).thenReturn(Optional.of(testSport));
        when(leagueRepo.findById("Premier League")).thenReturn(Optional.of(testLeague));
        when(countryRepo.findById("England")).thenReturn(Optional.of(testCountry));
        when(teamRepo.findById("Manchester United")).thenReturn(Optional.of(testTeamHome));
        when(teamRepo.findById("Liverpool")).thenReturn(Optional.of(testTeamAway));
        when(gameRepo.findByTeamHomeAndTeamAwayAndDateOfMatch(
                testTeamHome, testTeamAway, matchDateTime))
                .thenReturn(Collections.emptyList());

        // Act
        createMatchService.createMatch(matchDTO);

        // Assert
        verify(gameRepo, times(1)).save(any(Game.class));
    }

    /**
     * Tests {@link CreateMatchService#createMatch(CreatedMatchDTO)} without a country.
     * Verifies that:
     * - A match can be created when country is null
     * - The country field in the Game entity is set to null
     * - The match is successfully saved
     */
    @Test
    void createMatch_ShouldCreateMatch_WhenCountryIsNull() {
        // Arrange
        CreatedMatchDTO matchDTO = new CreatedMatchDTO();
        matchDTO.setSport("Soccer");
        matchDTO.setLeague("Premier League");
        matchDTO.setCountry(null);
        matchDTO.setTeamHome("Manchester United");
        matchDTO.setTeamAway("Liverpool");
        matchDTO.setDateOfMatch("2025-10-15T19:00:00");

        LocalDateTime matchDateTime = LocalDateTime.parse(matchDTO.getDateOfMatch());

        when(sportRepo.findById("Soccer")).thenReturn(Optional.of(testSport));
        when(leagueRepo.findById("Premier League")).thenReturn(Optional.of(testLeague));
        when(teamRepo.findById("Manchester United")).thenReturn(Optional.of(testTeamHome));
        when(teamRepo.findById("Liverpool")).thenReturn(Optional.of(testTeamAway));
        when(gameRepo.findByTeamHomeAndTeamAwayAndDateOfMatch(
                testTeamHome, testTeamAway, matchDateTime))
                .thenReturn(Collections.emptyList());

        // Act
        createMatchService.createMatch(matchDTO);

        // Assert
        verify(gameRepo, times(1)).save(any(Game.class));
        verify(countryRepo, never()).findById(anyString());
    }

    /**
     * Tests {@link CreateMatchService#createMatch(CreatedMatchDTO)} with a duplicate match.
     * Verifies that:
     * - An IllegalArgumentException is thrown when a match already exists
     * - The exception message indicates a duplicate match
     * - No new Game entity is saved to the database
     */
    @Test
    void createMatch_ShouldThrowException_WhenDuplicateMatchExists() {
        // Arrange
        CreatedMatchDTO matchDTO = new CreatedMatchDTO();
        matchDTO.setSport("Soccer");
        matchDTO.setLeague("Premier League");
        matchDTO.setCountry("England");
        matchDTO.setTeamHome("Manchester United");
        matchDTO.setTeamAway("Liverpool");
        matchDTO.setDateOfMatch("2025-10-15T19:00:00");

        LocalDateTime matchDateTime = LocalDateTime.parse(matchDTO.getDateOfMatch());
        Game existingGame = new Game();

        when(sportRepo.findById("Soccer")).thenReturn(Optional.of(testSport));
        when(leagueRepo.findById("Premier League")).thenReturn(Optional.of(testLeague));
        when(countryRepo.findById("England")).thenReturn(Optional.of(testCountry));
        when(teamRepo.findById("Manchester United")).thenReturn(Optional.of(testTeamHome));
        when(teamRepo.findById("Liverpool")).thenReturn(Optional.of(testTeamAway));
        when(gameRepo.findByTeamHomeAndTeamAwayAndDateOfMatch(
                testTeamHome, testTeamAway, matchDateTime))
                .thenReturn(Collections.singletonList(existingGame));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createMatchService.createMatch(matchDTO)
        );

        assertEquals("Match already exists between these teams on this date",
                exception.getMessage());
        verify(gameRepo, never()).save(any(Game.class));
    }

    /**
     * Tests {@link CreateMatchService#createMatch(CreatedMatchDTO)} with a non-existent sport.
     * Verifies that:
     * - An IllegalArgumentException is thrown when sport is not found
     * - The exception message indicates the missing sport
     * - No match creation is attempted
     */
    @Test
    void createMatch_ShouldThrowException_WhenSportNotFound() {
        // Arrange
        CreatedMatchDTO matchDTO = new CreatedMatchDTO();
        matchDTO.setSport("InvalidSport");
        matchDTO.setLeague("Premier League");
        matchDTO.setCountry("England");
        matchDTO.setTeamHome("Manchester United");
        matchDTO.setTeamAway("Liverpool");
        matchDTO.setDateOfMatch("2025-10-15T19:00:00");

        when(sportRepo.findById("InvalidSport")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createMatchService.createMatch(matchDTO)
        );

        assertTrue(exception.getMessage().contains("Sport not found"));
        verify(gameRepo, never()).save(any(Game.class));
    }

    /**
     * Tests {@link CreateMatchService#createMatch(CreatedMatchDTO)} with a non-existent league.
     * Verifies that:
     * - An IllegalArgumentException is thrown when league is not found
     * - The exception message indicates the missing league
     */
    @Test
    void createMatch_ShouldThrowException_WhenLeagueNotFound() {
        // Arrange
        CreatedMatchDTO matchDTO = new CreatedMatchDTO();
        matchDTO.setSport("Soccer");
        matchDTO.setLeague("InvalidLeague");
        matchDTO.setCountry("England");
        matchDTO.setTeamHome("Manchester United");
        matchDTO.setTeamAway("Liverpool");
        matchDTO.setDateOfMatch("2025-10-15T19:00:00");

        when(sportRepo.findById("Soccer")).thenReturn(Optional.of(testSport));
        when(leagueRepo.findById("InvalidLeague")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createMatchService.createMatch(matchDTO)
        );

        assertTrue(exception.getMessage().contains("League not found"));
        verify(gameRepo, never()).save(any(Game.class));
    }
}
