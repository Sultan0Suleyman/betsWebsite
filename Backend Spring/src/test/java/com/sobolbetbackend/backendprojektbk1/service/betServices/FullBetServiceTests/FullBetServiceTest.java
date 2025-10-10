package com.sobolbetbackend.backendprojektbk1.service.betServices.FullBetServiceTests;

import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList.FullBetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsInfo.betList.OrdinaryBetDTO;
import com.sobolbetbackend.backendprojektbk1.dto.placeBet.FullBetRequestDTO;
import com.sobolbetbackend.backendprojektbk1.dto.placeBet.OrdinaryBetRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.FullBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Team;
import com.sobolbetbackend.backendprojektbk1.exception.LowBalanceException;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.FullBetRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.OrdinaryBetRepo;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import com.sobolbetbackend.backendprojektbk1.service.betServices.FullBetService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FullBetService}.
 * This test class validates the functionality of the FullBetService including:
 * - Bet placement operations with balance validation
 * - Retrieval of bet history and details
 * - Dynamic sell price calculations
 * - Bet selling operations with proper financial processing
 * Uses Mockito framework for dependency injection and behavior verification.
 */
@ExtendWith(MockitoExtension.class)
class FullBetServiceTest {

    @Mock
    private FullBetRepo fullBetRepo;

    @Mock
    private OrdinaryBetRepo ordinaryBetRepo;

    @Mock
    private PlayerRepo playerRepo;

    @Mock
    private GameRepo gameRepo;

    @Mock  // Make Player a mock object for proper verification
    private Player mockPlayer;

    @InjectMocks
    private FullBetService fullBetService;

    private Game mockGame;
    private FullBet mockFullBet;
    private OrdinaryBet mockOrdinaryBet;
    private FullBetRequestDTO mockFullBetRequest;

    /**
     * Sets up test fixtures before each test method execution.
     * Initializes mock objects with default values and relationships.
     * Creates sample data for games, teams, bets, and request DTOs.
     */
    @BeforeEach
    void setUp() {
        // Setup mock game
        mockGame = new Game();
        mockGame.setId(1L);
        mockGame.setGameEnded(true);

        Team homeTeam = new Team();
        homeTeam.setName_en("Team A");
        Team awayTeam = new Team();
        awayTeam.setName_en("Team B");

        mockGame.setTeamHome(homeTeam);
        mockGame.setTeamAway(awayTeam);
        mockGame.setDateOfMatch(LocalDateTime.now().plusDays(1));

        // Setup mock full bet
        mockFullBet = new FullBet();
        mockFullBet.setId(1L);
        mockFullBet.setBetAmount(10.0);
        mockFullBet.setFinalCoefficient(2.0);
        mockFullBet.setPlayer(mockPlayer);

        // Setup mock ordinary bet
        mockOrdinaryBet = new OrdinaryBet();
        mockOrdinaryBet.setId(1L);
        mockOrdinaryBet.setGame(mockGame);
        mockOrdinaryBet.setFullBet(mockFullBet);
        mockOrdinaryBet.setOutcomeOfTheGame("Win1");
        mockOrdinaryBet.setCoefficient(2.0);
        mockOrdinaryBet.setWinningBet(true);

        mockFullBet.setBets(Collections.singletonList(mockOrdinaryBet));

        // Setup mock request
        OrdinaryBetRequestDTO ordinaryRequest = new OrdinaryBetRequestDTO();
        ordinaryRequest.setMatchId(1L);
        ordinaryRequest.setType("Win1");
        ordinaryRequest.setCoefficient(2.0);

        mockFullBetRequest = new FullBetRequestDTO();
        mockFullBetRequest.setUserId(1L);
        mockFullBetRequest.setBetAmount(10.0);
        mockFullBetRequest.setFinalCoefficient(2.0);
        mockFullBetRequest.setBets(Collections.singletonList(ordinaryRequest).toArray(new OrdinaryBetRequestDTO[0]));
    }

    // Tests for placeBet method

    /**
     * Tests successful bet placement when player has sufficient balance.
     * Verifies that bet is saved to repository and no exceptions are thrown.
     */
    @Test
    void placeBet_SufficientBalance_ShouldPlaceBetSuccessfully() {
        // Arrange
        when(mockPlayer.getBalance()).thenReturn(100.0);  // Configure mock with sufficient balance
        when(playerRepo.findByUserId(1L)).thenReturn(mockPlayer);
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));

        // Act
        assertDoesNotThrow(() -> fullBetService.placeBet(mockFullBetRequest));

        // Assert
        verify(fullBetRepo).save(any(FullBet.class));
    }

    /**
     * Tests bet placement failure when player has insufficient balance.
     * Verifies that LowBalanceException is thrown and no data is persisted.
     */
    @Test
    void placeBet_InsufficientBalance_ShouldThrowLowBalanceException() {
        // Arrange
        when(mockPlayer.getBalance()).thenReturn(5.0);  // Configure mock with insufficient balance
        when(playerRepo.findByUserId(1L)).thenReturn(mockPlayer);

        // Act & Assert
        LowBalanceException exception = assertThrows(LowBalanceException.class,
                () -> fullBetService.placeBet(mockFullBetRequest));
        assertEquals("Insufficient funds in the account", exception.getMessage());

        verify(fullBetRepo, never()).save(any());
        verify(ordinaryBetRepo, never()).saveAll(any());
    }

    // Tests for getListOfFullBets method

    /**
     * Tests retrieval of full bets list for existing user with bets.
     * Verifies correct mapping of FullBet entities to FullBetDTO objects.
     */
    @Test
    void getListOfFullBets_ExistingUser_ShouldReturnBetList() {
        // Arrange
        when(mockPlayer.getBets()).thenReturn(Collections.singletonList(mockFullBet));
        when(playerRepo.findByUserId(1L)).thenReturn(mockPlayer);

        // Act
        List<FullBetDTO> result = fullBetService.getListOfFullBets(1L);

        // Assert
        assertEquals(1, result.size());
        FullBetDTO betDTO = result.getFirst();
        assertEquals(mockFullBet.getId(), betDTO.getId());
        assertEquals(mockFullBet.getBetAmount(), betDTO.getBetAmount());
        assertEquals(mockFullBet.getFinalCoefficient(), betDTO.getFinalCoefficient());
        assertEquals(1, betDTO.getCountOfOrdinaryBets());
    }

    /**
     * Tests retrieval of empty bets list for user with no betting history.
     * Verifies that empty list is returned without errors.
     */
    @Test
    void getListOfFullBets_UserWithNoBets_ShouldReturnEmptyList() {
        // Arrange
        when(mockPlayer.getBets()).thenReturn(Collections.emptyList());
        when(playerRepo.findByUserId(1L)).thenReturn(mockPlayer);

        // Act
        List<FullBetDTO> result = fullBetService.getListOfFullBets(1L);

        // Assert
        assertTrue(result.isEmpty());
    }

    /**
     * Tests retrieval of multiple bets for user with extensive betting history.
     * Verifies correct handling and mapping of multiple FullBet entities.
     */
    @Test
    void getListOfFullBets_UserWithMultipleBets_ShouldReturnAllBets() {
        // Arrange
        FullBet secondBet = new FullBet();
        secondBet.setId(2L);
        secondBet.setBetAmount(20.0);
        secondBet.setFinalCoefficient(1.5);
        secondBet.setBets(Collections.emptyList());

        when(mockPlayer.getBets()).thenReturn(Arrays.asList(mockFullBet, secondBet));
        when(playerRepo.findByUserId(1L)).thenReturn(mockPlayer);

        // Act
        List<FullBetDTO> result = fullBetService.getListOfFullBets(1L);

        // Assert
        assertEquals(2, result.size());
    }

    // Tests for getListOfOrdinaryBets method

    /**
     * Tests retrieval of ordinary bets for existing full bet.
     * Verifies correct mapping of OrdinaryBet entities to OrdinaryBetDTO objects
     * including team names, outcomes, coefficients, and game status.
     */
    @Test
    void getListOfOrdinaryBets_ExistingFullBet_ShouldReturnOrdinaryBetsList() {
        // Arrange
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act
        List<OrdinaryBetDTO> result = fullBetService.getListOfOrdinaryBets(1L);

        // Assert
        assertEquals(1, result.size());
        OrdinaryBetDTO betDTO = result.getFirst();
        assertEquals("Team A", betDTO.getTeamHome());
        assertEquals("Team B", betDTO.getTeamAway());
        assertEquals("Win1", betDTO.getOutcomeOfTheGame());
        assertEquals(2.0, betDTO.getCoefficient());
        assertTrue(betDTO.getWinningBet());
    }

    /**
     * Tests handling of non-existent full bet ID.
     * Verifies that RuntimeException is thrown when full bet is not found.
     */
    @Test
    void getListOfOrdinaryBets_NonExistentFullBet_ShouldThrowException() {
        // Arrange
        when(fullBetRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> fullBetService.getListOfOrdinaryBets(999L));
    }

    /**
     * Tests retrieval of ordinary bets for express bet with multiple selections.
     * Verifies correct handling of complex betting structures with multiple outcomes.
     */
    @Test
    void getListOfOrdinaryBets_ExpressWithMultipleBets_ShouldReturnAllBets() {
        // Arrange
        OrdinaryBet secondOrdinaryBet = new OrdinaryBet();
        secondOrdinaryBet.setGame(mockGame);
        secondOrdinaryBet.setOutcomeOfTheGame("Draw");
        secondOrdinaryBet.setCoefficient(3.0);

        mockFullBet.setBets(Arrays.asList(mockOrdinaryBet, secondOrdinaryBet));
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act
        List<OrdinaryBetDTO> result = fullBetService.getListOfOrdinaryBets(1L);

        // Assert
        assertEquals(2, result.size());
    }

    // Tests for calculateSellPrice method

    /**
     * Tests sell price calculation for bet with no resolved outcomes.
     * Verifies that base price (90% of bet amount) is returned when no games are finished.
     */
    @Test
    void calculateSellPrice_NoWinningBets_ShouldReturnBasePriceNinetyPercent() {
        // Arrange
        mockOrdinaryBet.setWinningBet(null); // Not processed yet
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act
        double result = fullBetService.calculateSellPrice(1L);

        // Assert
        assertEquals(9.0, result, 0.01); // 90% of 10.0
    }

    /**
     * Tests sell price calculation for bet with one winning outcome.
     * Verifies correct application of coefficient multiplication in price formula.
     * Formula: base_price * 0.9 * coefficient = 9.0 * 0.9 * 2.0 = 16.2
     */
    @Test
    void calculateSellPrice_OneWinningBet_ShouldApplyCoefficient() {
        // Arrange
        mockOrdinaryBet.setWinningBet(true);
        mockOrdinaryBet.setCoefficient(2.0);
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act
        double result = fullBetService.calculateSellPrice(1L);

        // Assert
        assertEquals(16.2, result, 0.01); // 9.0 * 0.9 * 2.0
    }

    /**
     * Tests sell price calculation for bet with losing outcome.
     * Verifies that IllegalStateException is thrown when bet contains losing selections.
     */
    @Test
    void calculateSellPrice_BetWithLosingOutcome_ShouldThrowException() {
        // Arrange
        mockOrdinaryBet.setWinningBet(false);
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> fullBetService.calculateSellPrice(1L));
        assertEquals("Cannot sell bet with losing outcomes", exception.getMessage());
    }

    /**
     * Tests sell price calculation for already processed bet.
     * Verifies that IllegalStateException is thrown when attempting to calculate
     * price for bet that has already been settled.
     */
    @Test
    void calculateSellPrice_AlreadyProcessedBet_ShouldThrowException() {
        // Arrange
        mockFullBet.setFinalBetPayout(15.0); // Already processed
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> fullBetService.calculateSellPrice(1L));
        assertEquals("Bet already processed", exception.getMessage());
    }

    /**
     * Tests sell price calculation for express bet with refunded selection.
     * Verifies that refunded bets (null status) do not affect the calculated price.
     * Only winning bets should multiply the price by their coefficients.
     */
    @Test
    void calculateSellPrice_ExpressWithRefundBet_ShouldNotChangePrice() {
        // Arrange
        OrdinaryBet refundBet = new OrdinaryBet();
        refundBet.setGame(mockGame);
        refundBet.setWinningBet(null); // Refund
        refundBet.setCoefficient(1.5);

        mockOrdinaryBet.setWinningBet(true);
        mockFullBet.setBets(Arrays.asList(mockOrdinaryBet, refundBet));
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act
        double result = fullBetService.calculateSellPrice(1L);

        // Assert
        assertEquals(16.2, result, 0.01); // Only winning bet affects price
    }

    // Tests for sellBet method

    /**
     * Tests successful bet selling operation.
     * Verifies that sell price is calculated correctly, bet status is updated,
     * and player balance is credited with the appropriate amount.
     */
    @Test
    void sellBet_ValidBet_ShouldProcessSaleSuccessfully() {
        // Arrange
        mockOrdinaryBet.setWinningBet(true);
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act
        fullBetService.sellBet(1L);

        // Assert
        assertEquals(16.2, mockFullBet.getFinalBetPayout(), 0.01);
        assertNull(mockFullBet.getBetStatus()); // null indicates sold
        verify(mockPlayer).updateBalance(16.2);  // Verify balance update with correct amount
    }

    /**
     * Tests sell operation for already processed bet.
     * Verifies that no additional processing occurs when bet has already been settled.
     */
    @Test
    void sellBet_AlreadyProcessedBet_ShouldNotProcessAgain() {
        // Arrange
        mockFullBet.setFinalBetPayout(15.0); // Already processed
        when(fullBetRepo.findById(1L)).thenReturn(Optional.of(mockFullBet));

        // Act
        fullBetService.sellBet(1L);

        // Assert
        assertEquals(15.0, mockFullBet.getFinalBetPayout(), 0.01); // Unchanged
        verify(mockPlayer, never()).updateBalance(anyDouble());  // No balance update should occur
    }

    /**
     * Tests sell operation for non-existent bet.
     * Verifies that RuntimeException is thrown when attempting to sell
     * a bet that doesn't exist in the system.
     */
    @Test
    void sellBet_NonExistentBet_ShouldThrowException() {
        // Arrange
        when(fullBetRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> fullBetService.sellBet(999L));
    }
}