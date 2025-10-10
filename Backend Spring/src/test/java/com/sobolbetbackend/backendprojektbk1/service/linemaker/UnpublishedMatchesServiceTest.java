package com.sobolbetbackend.backendprojektbk1.service.linemaker;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches.*;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEvent;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEventStatus;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingOdd;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.*;
import com.sobolbetbackend.backendprojektbk1.repository.Linemaker.BettingEventRepo;
import com.sobolbetbackend.backendprojektbk1.repository.Linemaker.BettingOddRepo;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import com.sobolbetbackend.backendprojektbk1.service.linemakerServices.UnpublishedMatchesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UnpublishedMatchesService}.
 * This test class verifies the behavior of the UnpublishedMatchesService methods,
 * including match retrieval, status updates, deletion, odds management, and publishing logic.
 * Tests use Mockito to mock repository dependencies and validate service behavior.
 */
@ExtendWith(MockitoExtension.class)
class UnpublishedMatchesServiceTest {

    @Mock
    private GameRepo gameRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private BettingEventRepo bettingEventRepo;

    @Mock
    private BettingOddRepo bettingOddRepo;

    @InjectMocks
    private UnpublishedMatchesService unpublishedMatchesService;

    private Game testGame;
    private UserE testUser;
    private BettingEvent testBettingEvent;

    /**
     * Sets up test fixtures before each test method execution.
     * Initializes mock entities for Game, Sport, Country, League, Teams, User, and BettingEvent.
     */
    @BeforeEach
    void setUp() {
        Sport testSport = new Sport();
        testSport.setName_en("Soccer");

        Country testCountry = new Country();
        testCountry.setName("England");

        League testLeague = new League();
        testLeague.setName("Premier League");

        Team testTeamHome = new Team();
        testTeamHome.setName_en("Manchester United");

        Team testTeamAway = new Team();
        testTeamAway.setName_en("Liverpool");

        testGame = new Game();
        testGame.setId(1L);
        testGame.setSport(testSport);
        testGame.setCountry(testCountry);
        testGame.setLeague(testLeague);
        testGame.setTeamHome(testTeamHome);
        testGame.setTeamAway(testTeamAway);
        testGame.setDateOfMatch(LocalDateTime.of(2025, 10, 15, 19, 0));
        testGame.setGamePosted(false);
        testGame.setStatus(Game.Status.NONE);
        testGame.setLinemakersName(null);

        testUser = new UserE();
        testUser.setName("John");
        testUser.setSurname("Doe");

        testBettingEvent = new BettingEvent();
        testBettingEvent.setId(1L);
        testBettingEvent.setGame(testGame);
        testBettingEvent.setStatus(BettingEventStatus.DRAFT);
    }

    /**
     * Tests {@link UnpublishedMatchesService#getUnpublishedMatches()} when matches exist.
     * Verifies that:
     * - The method returns a list of unpublished match DTOs
     * - All match data is correctly mapped to DTOs
     * - Country names are properly handled (including null values)
     */
    @Test
    void getUnpublishedMatches_ShouldReturnListOfDTOs_WhenMatchesExist() {
        // Arrange
        when(gameRepo.findByIsGamePosted(false)).thenReturn(Collections.singletonList(testGame));

        // Act
        List<UnpublishedMatchDTO> result = unpublishedMatchesService.getUnpublishedMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        UnpublishedMatchDTO dto = result.getFirst();
        assertEquals("1", dto.getId());
        assertEquals("Soccer", dto.getSport());
        assertEquals("England", dto.getCountry());
        assertEquals("Premier League", dto.getLeague());
        assertEquals("Manchester United", dto.getTeamHome());
        assertEquals("Liverpool", dto.getTeamAway());
        assertEquals("NONE", dto.getStatus());
        assertNull(dto.getLinemakersName());
        verify(gameRepo, times(1)).findByIsGamePosted(false);
    }

    /**
     * Tests {@link UnpublishedMatchesService#getUnpublishedMatches()} with null country.
     * Verifies that:
     * - The method handles games without a country
     * - The country field in DTO is set to null
     * - No NullPointerException is thrown
     */
    @Test
    void getUnpublishedMatches_ShouldHandleNullCountry_WhenCountryIsNotSet() {
        // Arrange
        testGame.setCountry(null);
        when(gameRepo.findByIsGamePosted(false)).thenReturn(Collections.singletonList(testGame));

        // Act
        List<UnpublishedMatchDTO> result = unpublishedMatchesService.getUnpublishedMatches();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.getFirst().getCountry());
        verify(gameRepo, times(1)).findByIsGamePosted(false);
    }

    /**
     * Tests {@link UnpublishedMatchesService#getUnpublishedMatches()} when no matches exist.
     * Verifies that:
     * - The method returns an empty list
     * - No exceptions are thrown
     */
    @Test
    void getUnpublishedMatches_ShouldReturnEmptyList_WhenNoMatchesExist() {
        // Arrange
        when(gameRepo.findByIsGamePosted(false)).thenReturn(Collections.emptyList());

        // Act
        List<UnpublishedMatchDTO> result = unpublishedMatchesService.getUnpublishedMatches();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(gameRepo, times(1)).findByIsGamePosted(false);
    }

    /**
     * Tests {@link UnpublishedMatchesService#getLinemakersNameSurname(String)} with valid user ID.
     * Verifies that:
     * - The method returns user's name and surname
     * - User ID is correctly parsed and used to fetch data
     */
    @Test
    void getLinemakersNameSurname_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(userRepo.findById(1L)).thenReturn(testUser);

        // Act
        LinemakersNameSurnameDTO result = unpublishedMatchesService.getLinemakersNameSurname("1");

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(userRepo, times(1)).findById(1L);
    }

    /**
     * Tests {@link UnpublishedMatchesService#updateMatchStatus(UpdateMatchStatusDTO)} with valid data.
     * Verifies that:
     * - Match status is successfully updated
     * - Linemaker's name is set correctly
     * - The updated match is saved to the database
     */
    @Test
    void updateMatchStatus_ShouldUpdateStatus_WhenMatchExists() {
        // Arrange
        UpdateMatchStatusDTO dto = new UpdateMatchStatusDTO();
        dto.setMatchId(1L);
        dto.setStatus("IN_PROGRESS");
        dto.setLinemakersName("John Doe");

        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepo.save(any(Game.class))).thenReturn(testGame);

        // Act
        unpublishedMatchesService.updateMatchStatus(dto);

        // Assert
        assertEquals(Game.Status.IN_PROGRESS, testGame.getStatus());
        assertEquals("John Doe", testGame.getLinemakersName());
        verify(gameRepo, times(1)).findById(1L);
        verify(gameRepo, times(1)).save(testGame);
    }

    /**
     * Tests {@link UnpublishedMatchesService#updateMatchStatus(UpdateMatchStatusDTO)} when match not found.
     * Verifies that:
     * - A RuntimeException is thrown when match doesn't exist
     * - The exception message indicates the match ID
     * - No save operation is performed
     */
    @Test
    void updateMatchStatus_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        UpdateMatchStatusDTO dto = new UpdateMatchStatusDTO();
        dto.setMatchId(999L);
        dto.setStatus("IN_PROGRESS");
        dto.setLinemakersName("John Doe");

        when(gameRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> unpublishedMatchesService.updateMatchStatus(dto)
        );

        assertTrue(exception.getMessage().contains("Match with id 999 not found"));
        verify(gameRepo, never()).save(any(Game.class));
    }

    /**
     * Tests {@link UnpublishedMatchesService#deleteMatch(Long)} with valid unpublished match.
     * Verifies that:
     * - An unpublished match can be deleted successfully
     * - The delete operation is called on the repository
     */
    @Test
    void deleteMatch_ShouldDeleteMatch_WhenMatchIsUnpublished() {
        // Arrange
        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        doNothing().when(gameRepo).delete(testGame);

        // Act
        unpublishedMatchesService.deleteMatch(1L);

        // Assert
        verify(gameRepo, times(1)).findById(1L);
        verify(gameRepo, times(1)).delete(testGame);
    }

    /**
     * Tests {@link UnpublishedMatchesService#deleteMatch(Long)} when match is already published.
     * Verifies that:
     * - A RuntimeException is thrown for published matches
     * - The exception message indicates the match cannot be deleted
     * - No deletion occurs
     */
    @Test
    void deleteMatch_ShouldThrowException_WhenMatchIsPublished() {
        // Arrange
        testGame.setGamePosted(true);
        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> unpublishedMatchesService.deleteMatch(1L)
        );

        assertTrue(exception.getMessage().contains("Cannot delete published match"));
        verify(gameRepo, never()).delete(any(Game.class));
    }

    /**
     * Tests {@link UnpublishedMatchesService#deleteMatch(Long)} when match doesn't exist.
     * Verifies that:
     * - A RuntimeException is thrown
     * - The exception message indicates the match was not found
     */
    @Test
    void deleteMatch_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        when(gameRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> unpublishedMatchesService.deleteMatch(999L)
        );

        assertTrue(exception.getMessage().contains("Match with id 999 not found"));
        verify(gameRepo, never()).delete(any(Game.class));
    }

    /**
     * Tests {@link UnpublishedMatchesService#getUnpublishedMatchOddsDetails(Long)} with existing odds.
     * Verifies that:
     * - Match details are correctly returned
     * - Odds are properly mapped to the DTO
     * - All match attributes are included
     */
    @Test
    void getUnpublishedMatchOddsDetails_ShouldReturnDetailsWithOdds_WhenMatchAndOddsExist() {
        // Arrange
        BettingOdd odd1 = new BettingOdd();
        odd1.setType("win1");
        odd1.setValue(1.75);

        BettingOdd odd2 = new BettingOdd();
        odd2.setType("draw");
        odd2.setValue(3.5);

        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(bettingEventRepo.findByGame(testGame)).thenReturn(Optional.of(testBettingEvent));
        when(bettingOddRepo.findAllByBettingEvent(testBettingEvent))
                .thenReturn(Arrays.asList(odd1, odd2));

        // Act
        UnpublishedMatchOddsDetailsDTO result = unpublishedMatchesService.getUnpublishedMatchOddsDetails(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Soccer", result.getSport());
        assertEquals("England", result.getCountry());
        assertEquals("Premier League", result.getLeague());
        assertEquals("Manchester United", result.getTeamHome());
        assertEquals("Liverpool", result.getTeamAway());
        assertNotNull(result.getOdds());
        assertEquals(2, result.getOdds().size());
        assertEquals(1.75, result.getOdds().get("win1"));
        assertEquals(3.5, result.getOdds().get("draw"));
        verify(gameRepo, times(1)).findById(1L);
    }

    /**
     * Tests {@link UnpublishedMatchesService#getUnpublishedMatchOddsDetails(Long)} without betting event.
     * Verifies that:
     * - Match details are returned even without odds
     * - The odds map is empty
     * - No exception is thrown
     */
    @Test
    void getUnpublishedMatchOddsDetails_ShouldReturnDetailsWithoutOdds_WhenNoBettingEventExists() {
        // Arrange
        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(bettingEventRepo.findByGame(testGame)).thenReturn(Optional.empty());

        // Act
        UnpublishedMatchOddsDetailsDTO result = unpublishedMatchesService.getUnpublishedMatchOddsDetails(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getOdds());
        assertTrue(result.getOdds().isEmpty());
        verify(bettingOddRepo, never()).findAllByBettingEvent(any());
    }

    /**
     * Tests {@link UnpublishedMatchesService#getUnpublishedMatchOddsDetails(Long)} when match not found.
     * Verifies that:
     * - A RuntimeException is thrown
     * - The exception message indicates the match was not found
     */
    @Test
    void getUnpublishedMatchOddsDetails_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        when(gameRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> unpublishedMatchesService.getUnpublishedMatchOddsDetails(999L)
        );

        assertTrue(exception.getMessage().contains("Match not found with id: 999"));
    }

    /**
     * Tests {@link UnpublishedMatchesService#saveOdds(SetOddsRequestDTO)} for a new betting event.
     * Verifies that:
     * - A new BettingEvent is created if it doesn't exist
     * - All odds are saved correctly
     * - The betting event status is set to DRAFT
     */
    @Test
    void saveOdds_ShouldCreateNewBettingEventAndSaveOdds_WhenNoBettingEventExists() {
        // Arrange
        SetOddsRequestDTO request = new SetOddsRequestDTO();
        request.setMatchId(1L);
        Map<String, Double> odds = new HashMap<>();
        odds.put("win1", 1.75);
        odds.put("draw", 3.5);
        odds.put("win2", 2.25);
        request.setOdds(odds);

        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(bettingEventRepo.findByGame(testGame)).thenReturn(Optional.empty());
        when(bettingEventRepo.save(any(BettingEvent.class))).thenReturn(testBettingEvent);
        when(bettingOddRepo.findAllByBettingEvent(testBettingEvent)).thenReturn(Collections.emptyList());
        when(bettingOddRepo.save(any(BettingOdd.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        unpublishedMatchesService.saveOdds(request);

        // Assert
        verify(bettingEventRepo, times(1)).save(any(BettingEvent.class));
        verify(bettingOddRepo, times(3)).save(any(BettingOdd.class));
    }

    /**
     * Tests {@link UnpublishedMatchesService#saveOdds(SetOddsRequestDTO)} for updating existing odds.
     * Verifies that:
     * - Existing odds are deleted before saving new ones
     * - New odds are saved correctly
     * - The existing BettingEvent is reused
     */
    @Test
    void saveOdds_ShouldUpdateExistingOdds_WhenBettingEventExists() {
        // Arrange
        SetOddsRequestDTO request = new SetOddsRequestDTO();
        request.setMatchId(1L);
        Map<String, Double> odds = new HashMap<>();
        odds.put("win1", 2.0);
        odds.put("draw", 3.0);
        request.setOdds(odds);

        BettingOdd existingOdd = new BettingOdd();
        existingOdd.setType("win1");
        existingOdd.setValue(1.5);

        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(bettingEventRepo.findByGame(testGame)).thenReturn(Optional.of(testBettingEvent));
        when(bettingOddRepo.findAllByBettingEvent(testBettingEvent))
                .thenReturn(Collections.singletonList(existingOdd));
        when(bettingOddRepo.save(any(BettingOdd.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        unpublishedMatchesService.saveOdds(request);

        // Assert
        verify(bettingOddRepo, times(1)).deleteAll(anyList());
        verify(bettingOddRepo, times(2)).save(any(BettingOdd.class));
        verify(bettingEventRepo, never()).save(any(BettingEvent.class));
    }

    /**
     * Tests {@link UnpublishedMatchesService#saveOdds(SetOddsRequestDTO)} with null odd values.
     * Verifies that:
     * - Null odd values are skipped during save
     * - Only non-null odds are persisted
     */
    @Test
    void saveOdds_ShouldSkipNullOdds_WhenOddValuesAreNull() {
        // Arrange
        SetOddsRequestDTO request = new SetOddsRequestDTO();
        request.setMatchId(1L);
        Map<String, Double> odds = new HashMap<>();
        odds.put("win1", 1.75);
        odds.put("draw", null);
        odds.put("win2", 2.25);
        request.setOdds(odds);

        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(bettingEventRepo.findByGame(testGame)).thenReturn(Optional.of(testBettingEvent));
        when(bettingOddRepo.findAllByBettingEvent(testBettingEvent)).thenReturn(Collections.emptyList());
        when(bettingOddRepo.save(any(BettingOdd.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        unpublishedMatchesService.saveOdds(request);

        // Assert
        verify(bettingOddRepo, times(2)).save(any(BettingOdd.class)); // Only 2 non-null odds
    }

    /**
     * Tests {@link UnpublishedMatchesService#saveOdds(SetOddsRequestDTO)} when game not found.
     * Verifies that:
     * - An IllegalArgumentException is thrown
     * - No odds are saved
     */
    @Test
    void saveOdds_ShouldThrowException_WhenGameNotFound() {
        // Arrange
        SetOddsRequestDTO request = new SetOddsRequestDTO();
        request.setMatchId(999L);
        request.setOdds(new HashMap<>());

        when(gameRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> unpublishedMatchesService.saveOdds(request)
        );

        assertTrue(exception.getMessage().contains("Game not found with id: 999"));
        verify(bettingOddRepo, never()).save(any(BettingOdd.class));
    }

    /**
     * Tests {@link UnpublishedMatchesService#publishMatch(Long)} with valid match.
     * Verifies that:
     * - Match is marked as posted
     * - Betting event status is changed to PUBLISHED
     * - Changes are saved to the database
     */
    @Test
    void publishMatch_ShouldPublishMatch_WhenMatchExists() {
        // Arrange
        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(bettingEventRepo.findByGame(testGame)).thenReturn(Optional.of(testBettingEvent));
        when(gameRepo.save(any(Game.class))).thenReturn(testGame);

        // Act
        unpublishedMatchesService.publishMatch(1L);

        // Assert
        assertTrue(testGame.getGamePosted());
        assertEquals(BettingEventStatus.PUBLISHED, testBettingEvent.getStatus());
        verify(gameRepo, times(1)).save(testGame);
    }

    /**
     * Tests {@link UnpublishedMatchesService#publishMatch(Long)} when match not found.
     * Verifies that:
     * - A RuntimeException is thrown
     * - No changes are saved
     */
    @Test
    void publishMatch_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        when(gameRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> unpublishedMatchesService.publishMatch(999L)
        );

        assertTrue(exception.getMessage().contains("Match not found with id: 999"));
        verify(gameRepo, never()).save(any(Game.class));
    }

    /**
     * Tests {@link UnpublishedMatchesService#unPublishMatch(Long)} with valid match.
     * Verifies that:
     * - Match is marked as unpublished
     * - Betting event status is changed to DRAFT
     * - Changes are saved to the database
     */
    @Test
    void unPublishMatch_ShouldUnpublishMatch_WhenMatchExists() {
        // Arrange
        testGame.setGamePosted(true);
        testBettingEvent.setStatus(BettingEventStatus.PUBLISHED);

        when(gameRepo.findById(1L)).thenReturn(Optional.of(testGame));
        when(bettingEventRepo.findByGame(testGame)).thenReturn(Optional.of(testBettingEvent));
        when(gameRepo.save(any(Game.class))).thenReturn(testGame);

        // Act
        unpublishedMatchesService.unPublishMatch(1L);

        // Assert
        assertFalse(testGame.getGamePosted());
        assertEquals(BettingEventStatus.DRAFT, testBettingEvent.getStatus());
        verify(gameRepo, times(1)).save(testGame);
    }

    /**
     * Tests {@link UnpublishedMatchesService#unPublishMatch(Long)} when match not found.
     * Verifies that:
     * - A RuntimeException is thrown
     * - No changes are saved
     */
    @Test
    void unPublishMatch_ShouldThrowException_WhenMatchNotFound() {
        // Arrange
        when(gameRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> unpublishedMatchesService.unPublishMatch(999L)
        );

        assertTrue(exception.getMessage().contains("Match not found with id: 999"));
        verify(gameRepo, never()).save(any(Game.class));
    }
}
