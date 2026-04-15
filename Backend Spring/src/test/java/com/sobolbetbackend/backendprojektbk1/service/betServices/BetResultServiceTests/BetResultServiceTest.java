package com.sobolbetbackend.backendprojektbk1.service.betServices.BetResultServiceTests;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.BetCalculationResultDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.RefundResultDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.FullBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.FullBetRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.OrdinaryBetRepo;
import com.sobolbetbackend.backendprojektbk1.service.matchSettlementServices.BetResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for BetResultService class.
 * This test class provides comprehensive coverage for all major methods in BetResultService,
 * including bet calculation logic, express bet processing, and game cancellation functionality.
 * Uses Mockito framework to isolate the service from external dependencies and ensure
 * focused testing of business logic.
 * Test coverage includes:
 * - Game result processing and bet calculations
 * - Single bet outcome determination for various bet types
 * - Express bet evaluation and payout logic
 * - Game cancellation and refund processing
 * - Error handling for edge cases and invalid states
 */
@ExtendWith(MockitoExtension.class)
class BetResultServiceTest {

    /** Mock repository for game data access operations */
    @Mock
    private GameRepo gameRepo;

    /** Mock repository for ordinary bet management */
    @Mock
    private OrdinaryBetRepo ordinaryBetRepo;

    /** Mock repository for full bet management */
    @Mock
    private FullBetRepo fullBetRepo;

    /** Service under test with mocked dependencies injected */
    @InjectMocks
    private BetResultService betResultService;

    /** Mock game object used in test scenarios */
    private Game mockGame;

    /** Mock full bet object representing single or express bets */
    private FullBet mockFullBet;

    /** Mock ordinary bet object representing individual bet outcomes */
    private OrdinaryBet mockOrdinaryBet;

    /**
     * Sets up test fixtures before each test method execution.
     * Creates mock objects with predefined states to ensure consistent test conditions.
     * Configures a winning scenario by default: home team wins 2:1, bet is on home team victory.
     */
    @BeforeEach
    void setUp() {
        mockGame = new Game();
        mockGame.setId(1L);
        mockGame.setGameEnded(true);
        mockGame.setScoreTeamHome(2);
        mockGame.setScoreTeamAway(1);

        Player mockPlayer = new Player();
        mockPlayer.setId(1L);
        mockPlayer.setBalance(100.0);

        mockFullBet = new FullBet();
        mockFullBet.setId(1L);
        mockFullBet.setBetAmount(10.0);
        mockFullBet.setFinalCoefficient(2.0);
        mockFullBet.setPlayer(mockPlayer);

        mockOrdinaryBet = new OrdinaryBet();
        mockOrdinaryBet.setId(1L);
        mockOrdinaryBet.setGame(mockGame);
        mockOrdinaryBet.setFullBet(mockFullBet);
        mockOrdinaryBet.setOutcomeOfTheGame("Win1");
        mockOrdinaryBet.setCoefficient(2.0);

        mockFullBet.setBets(Collections.singletonList(mockOrdinaryBet));
    }

    /**
     * Tests successful processing of a winning single bet.
     * Verifies that the service correctly calculates payouts, updates statistics,
     * and persists changes when processing a straightforward winning bet scenario.
     * Expected behavior:
     * - Processes exactly one bet
     * - Marks bet as winning
     * - Calculates correct payout (bet amount × coefficient)
     * - Updates bookmaker profit correctly
     * - Calls repository save methods
     */
    @Test
    void processGameResult_SuccessfulSingleBetWin_ShouldReturnCorrectResult() {
        // Arrange
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        BetCalculationResultDTO result = betResultService.processGameResult(1L);

        // Assert
        assertEquals(1, result.getTotalBetsProcessed());
        assertEquals(1, result.getWinningBets());
        assertEquals(0, result.getLosingBets());
        assertEquals(20.0, result.getTotalPayouts(), 0.01);
        assertEquals(-10.0, result.getBookmakerProfit(), 0.01);
        verify(ordinaryBetRepo).saveAll(any());
        verify(fullBetRepo).saveAll(any());
    }

    /**
     * Tests error handling when attempting to process results for a non-existent game.
     * Ensures the service throws appropriate exception with descriptive message
     * when game lookup fails.
     */
    @Test
    void processGameResult_GameNotFound_ShouldThrowException() {
        // Arrange
        when(gameRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> betResultService.processGameResult(1L));
        assertEquals("Game not found with id: 1", exception.getMessage());
    }

    /**
     * Tests error handling when attempting to process results for an unfinished game.
     * Verifies that the service prevents processing of games that haven't ended yet.
     */
    @Test
    void processGameResult_UnfinishedGame_ShouldThrowException() {
        // Arrange
        mockGame.setGameEnded(false);
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> betResultService.processGameResult(1L));
        assertEquals("Cannot process bets for unfinished game", exception.getMessage());
    }

    /**
     * Tests single bet calculation logic for a winning home team bet.
     * Verifies that the calculateSingleBetResult method correctly identifies
     * a winning "Win1" bet when the home team has more goals.
     */
    @Test
    void calculateSingleBetResult_WinningHomeBet_ShouldReturnTrue() {
        // Arrange
        mockGame.setScoreTeamHome(2);
        mockGame.setScoreTeamAway(1);
        mockOrdinaryBet.setOutcomeOfTheGame("Win1");
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        BetCalculationResultDTO result = betResultService.processGameResult(1L);

        // Assert
        assertEquals(1, result.getWinningBets());
        assertEquals(0, result.getLosingBets());
        assertTrue(mockOrdinaryBet.getWinningBet());
    }

    /**
     * Tests single bet calculation logic for a draw bet.
     * Verifies correct identification of winning draw bets when the final score is tied.
     */
    @Test
    void calculateSingleBetResult_DrawBet_ShouldReturnCorrectResult() {
        // Arrange
        mockGame.setScoreTeamHome(1);
        mockGame.setScoreTeamAway(1);
        mockOrdinaryBet.setOutcomeOfTheGame("Draw");
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        BetCalculationResultDTO result = betResultService.processGameResult(1L);

        // Assert
        assertEquals(1, result.getWinningBets());
        assertTrue(mockOrdinaryBet.getWinningBet());
    }

    /**
     * Tests single bet calculation logic for a losing bet.
     * Verifies that bets are correctly marked as losing when the predicted outcome
     * does not match the actual game result.
     */
    @Test
    void calculateSingleBetResult_LosingBet_ShouldReturnFalse() {
        // Arrange
        mockGame.setScoreTeamHome(1);
        mockGame.setScoreTeamAway(2);
        mockOrdinaryBet.setOutcomeOfTheGame("Win1");
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        BetCalculationResultDTO result = betResultService.processGameResult(1L);

        // Assert
        assertEquals(0, result.getWinningBets());
        assertEquals(1, result.getLosingBets());
        assertFalse(mockOrdinaryBet.getWinningBet());
    }

    /**
     * Tests express bet evaluation when all constituent bets are winners.
     * Verifies that express bets correctly pay out when all individual bets
     * within the combination have winning outcomes.
     */
    @Test
    void isFullBetWon_AllBetsWon_ShouldReturnTrue() {
        // Arrange
        OrdinaryBet secondBet = new OrdinaryBet();
        secondBet.setGame(mockGame);
        secondBet.setOutcomeOfTheGame("Win1");
        secondBet.setWinningBet(true);

        mockOrdinaryBet.setWinningBet(true);
        mockFullBet.setBets(Arrays.asList(mockOrdinaryBet, secondBet));

        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        BetCalculationResultDTO result = betResultService.processGameResult(1L);

        // Assert
        assertEquals(20.0, result.getTotalPayouts(), 0.01);
        assertTrue(mockFullBet.getBetStatus());
    }

    /**
     * Tests express bet evaluation when at least one constituent bet loses.
     * Verifies that express bets are marked as losing and receive no payout
     * when any individual bet within the combination fails.
     */
    @Test
    void isFullBetWon_OneBetLost_ShouldReturnFalse() {
        // Arrange
        mockGame.setScoreTeamHome(1);
        mockGame.setScoreTeamAway(2);
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        BetCalculationResultDTO result = betResultService.processGameResult(1L);

        // Assert
        assertEquals(0.0, result.getTotalPayouts(), 0.01);
        assertFalse(mockFullBet.getBetStatus());
    }

    /**
     * Tests express bet behavior when not all constituent games have finished.
     * Verifies that express bets remain unprocessed (null status and payout)
     * when some games in the combination are still ongoing.
     */
    @Test
    void isFullBetWon_GameNotFinished_ShouldNotProcessBet() {
        // Arrange
        Game unfinishedGame = new Game();
        unfinishedGame.setId(2L);
        unfinishedGame.setGameEnded(false);

        OrdinaryBet betOnUnfinishedGame = new OrdinaryBet();
        betOnUnfinishedGame.setGame(unfinishedGame);
        betOnUnfinishedGame.setFullBet(mockFullBet);

        mockFullBet.setBets(Arrays.asList(mockOrdinaryBet, betOnUnfinishedGame));

        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        BetCalculationResultDTO result = betResultService.processGameResult(1L);

        // Assert
        assertEquals(0.0, result.getTotalPayouts(), 0.01);
        assertNull(mockFullBet.getBetStatus());
        assertNull(mockFullBet.getFinalBetPayout());
    }

    /**
     * Tests game cancellation for single bets with full refund processing.
     * Verifies that cancelled single bets receive full refunds with coefficient set to 1.0,
     * players' balances are updated, and all changes are persisted.
     */
    @Test
    void cancelGame_SingleBet_ShouldRefundFullAmount() {
        // Arrange
        mockGame.setGameEnded(false);
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        RefundResultDTO result = betResultService.cancelGame(1L);

        // Assert
        assertEquals(1, result.getTotalBetsRefunded());
        assertEquals(1, result.getRefundedBets());
        assertEquals(10.0, result.getTotalRefundAmount(), 0.01);
        assertEquals(1.0, mockFullBet.getFinalCoefficient(), 0.01);
        assertNull(mockFullBet.getBetStatus());
        assertNull(mockOrdinaryBet.getWinningBet());
        verify(gameRepo).save(mockGame);
        verify(ordinaryBetRepo).saveAll(any());
        verify(fullBetRepo).saveAll(any());
    }

    /**
     * Tests game cancellation for express bets with coefficient adjustment.
     * Verifies that when a game in an express bet is cancelled, the final coefficient
     * is adjusted by dividing out the cancelled bet's coefficient, and if all other
     * games are finished and won, the express pays out with the adjusted coefficient.
     */
    @Test
    void cancelGame_ExpressBetWithAdjustedCoefficient_ShouldUpdateCoefficient() {
        // Arrange
        mockGame.setGameEnded(false);

        // Create express bet with 2 games
        Game secondGame = new Game();
        secondGame.setId(2L);
        secondGame.setGameEnded(true);

        OrdinaryBet secondBet = new OrdinaryBet();
        secondBet.setId(2L);
        secondBet.setGame(secondGame);
        secondBet.setFullBet(mockFullBet);
        secondBet.setCoefficient(3.0);
        secondBet.setWinningBet(true);

        mockFullBet.setFinalCoefficient(6.0); // 2.0 * 3.0
        mockFullBet.setBets(Arrays.asList(mockOrdinaryBet, secondBet));

        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));
        when(ordinaryBetRepo.findByGameId(1L)).thenReturn(Collections.singletonList(mockOrdinaryBet));

        // Act
        RefundResultDTO result = betResultService.cancelGame(1L);

        // Assert
        assertEquals(1, result.getTotalBetsRefunded());
        assertEquals(1, result.getRefundedBets());
        assertEquals(30.0, result.getTotalRefundAmount(), 0.01); // 10 * 3.0 (adjusted coefficient)
        assertEquals(3.0, mockFullBet.getFinalCoefficient(), 0.01); // 6.0 / 2.0
        assertTrue(mockFullBet.getBetStatus());
    }

    /**
     * Tests error handling when attempting to cancel an already finished game.
     * Verifies that the service prevents cancellation of games that have already
     * ended and potentially had their bets processed.
     */
    @Test
    void cancelGame_AlreadyFinishedGame_ShouldThrowException() {
        // Arrange
        mockGame.setGameEnded(true);
        when(gameRepo.findById(1L)).thenReturn(Optional.of(mockGame));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> betResultService.cancelGame(1L));
        assertEquals("Cannot cancel already finished game", exception.getMessage());
    }
}