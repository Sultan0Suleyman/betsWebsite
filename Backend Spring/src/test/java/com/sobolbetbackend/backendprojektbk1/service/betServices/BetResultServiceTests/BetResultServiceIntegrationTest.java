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
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import com.sobolbetbackend.backendprojektbk1.service.matchSettlementServices.BetResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
@DisplayName("BetResultService Integration Tests")
class BetResultServiceIntegrationTest {

    @Autowired
    private BetResultService betResultService;

    @Autowired
    private GameRepo gameRepo;

    @Autowired
    private OrdinaryBetRepo ordinaryBetRepo;

    @Autowired
    private FullBetRepo fullBetRepo;

    @Autowired
    private PlayerRepo playerRepo;

    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    void setUp() {
        // Create test player
        testPlayer = new Player();
        testPlayer.getUser().setEmail("testplayer@example.com");
        testPlayer.getUser().setName("Test");
        testPlayer.getUser().setSurname("Player");
        testPlayer.getUser().setNumberOfPassport("AB123456");
        testPlayer.getUser().setPassportIssueDate(java.time.LocalDate.of(2020, 1, 1));
        testPlayer.getUser().setPassportIssuingAuthority("Test Authority");
        testPlayer.getUser().setPassword("password");
        testPlayer.setBalance(1000.0);
        testPlayer = playerRepo.save(testPlayer);

        // Create simple test game
        testGame = new Game();
        testGame.setGameEnded(true);
        testGame.setScoreTeamHome(2);
        testGame.setScoreTeamAway(1);
        testGame = gameRepo.save(testGame);
    }

    @Test
    @DisplayName("Should process winning single bet correctly")
    void shouldProcessWinningSingleBetCorrectly() {
        // Given
        FullBet fullBet = createFullBet(100.0, 2.5);
        OrdinaryBet ordinaryBet = createOrdinaryBet(testGame, fullBet, "Win1", 2.5);

        // When
        BetCalculationResultDTO result = betResultService.processGameResult(testGame.getId());

        // Then
        assertThat(result.getTotalBetsProcessed()).isEqualTo(1);
        assertThat(result.getWinningBets()).isEqualTo(1);
        assertThat(result.getLosingBets()).isEqualTo(0);
        assertThat(result.getTotalPayouts()).isEqualTo(250.0); // 100 * 2.5
        assertThat(result.getBookmakerProfit()).isEqualTo(-150.0); // 100 - 250

        // Verify bet status
        OrdinaryBet updatedBet = ordinaryBetRepo.findById(ordinaryBet.getId()).get();
        assertThat(updatedBet.getWinningBet()).isTrue();

        // Verify payout
        FullBet updatedFullBet = fullBetRepo.findById(fullBet.getId()).get();
        assertThat(updatedFullBet.getFinalBetPayout()).isEqualTo(250.0);
        assertThat(updatedFullBet.getBetStatus()).isTrue();

        // Verify player balance update
        Player updatedPlayer = playerRepo.findById(testPlayer.getId()).get();
        assertThat(updatedPlayer.getBalance()).isEqualTo(1250.0); // 1000 + 250
    }

    @Test
    @DisplayName("Should process losing single bet correctly")
    void shouldProcessLosingSingleBetCorrectly() {
        // Given
        FullBet fullBet = createFullBet(50.0, 3.0);
        OrdinaryBet ordinaryBet = createOrdinaryBet(testGame, fullBet, "Win2", 3.0); // Barcelona loses

        // When
        BetCalculationResultDTO result = betResultService.processGameResult(testGame.getId());

        // Then
        assertThat(result.getTotalBetsProcessed()).isEqualTo(1);
        assertThat(result.getWinningBets()).isEqualTo(0);
        assertThat(result.getLosingBets()).isEqualTo(1);
        assertThat(result.getTotalPayouts()).isEqualTo(0.0);
        assertThat(result.getBookmakerProfit()).isEqualTo(50.0);

        // Verify bet status
        OrdinaryBet updatedBet = ordinaryBetRepo.findById(ordinaryBet.getId()).get();
        assertThat(updatedBet.getWinningBet()).isFalse();

        // Verify no payout
        FullBet updatedFullBet = fullBetRepo.findById(fullBet.getId()).get();
        assertThat(updatedFullBet.getFinalBetPayout()).isEqualTo(0.0);
        assertThat(updatedFullBet.getBetStatus()).isFalse();

        // Verify player balance unchanged
        Player updatedPlayer = playerRepo.findById(testPlayer.getId()).get();
        assertThat(updatedPlayer.getBalance()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("Should process winning express bet correctly")
    void shouldProcessWinningExpressBetCorrectly() {
        // Given - Create second finished game
        Game game2 = new Game();
        game2.setGameEnded(true);
        game2.setScoreTeamHome(1);
        game2.setScoreTeamAway(0);
        game2 = gameRepo.save(game2);

        FullBet expressBet = createFullBet(100.0, 6.0); // 2.5 * 2.4
        OrdinaryBet bet1 = createOrdinaryBet(testGame, expressBet, "Win1", 2.5); // Real Madrid wins
        OrdinaryBet bet2 = createOrdinaryBet(game2, expressBet, "Win1", 2.4); // Liverpool wins

        // When - Process both games
        betResultService.processGameResult(testGame.getId());
        BetCalculationResultDTO result = betResultService.processGameResult(game2.getId());

        // Then
        assertThat(result.getTotalPayouts()).isEqualTo(600.0); // 100 * 6.0

        // Verify express bet
        FullBet updatedExpressBet = fullBetRepo.findById(expressBet.getId()).get();
        assertThat(updatedExpressBet.getFinalBetPayout()).isEqualTo(600.0);
        assertThat(updatedExpressBet.getBetStatus()).isTrue();

        // Verify both ordinary bets won
        assertThat(ordinaryBetRepo.findById(bet1.getId()).get().getWinningBet()).isTrue();
        assertThat(ordinaryBetRepo.findById(bet2.getId()).get().getWinningBet()).isTrue();
    }

    @Test
    @DisplayName("Should process losing express bet when one bet loses")
    void shouldProcessLosingExpressBetWhenOneBetLoses() {
        // Given - Create second finished game
        Game game2 = new Game();
        game2.setGameEnded(true);
        game2.setScoreTeamHome(0);
        game2.setScoreTeamAway(2);
        game2 = gameRepo.save(game2);

        FullBet expressBet = createFullBet(100.0, 6.0);
        OrdinaryBet bet1 = createOrdinaryBet(testGame, expressBet, "Win1", 2.5); // Real Madrid wins
        OrdinaryBet bet2 = createOrdinaryBet(game2, expressBet, "Win1", 2.4); // Liverpool loses

        // When - Process both games
        betResultService.processGameResult(testGame.getId());
        BetCalculationResultDTO result = betResultService.processGameResult(game2.getId());

        // Then
        FullBet updatedExpressBet = fullBetRepo.findById(expressBet.getId()).get();
        assertThat(updatedExpressBet.getFinalBetPayout()).isEqualTo(0.0);
        assertThat(updatedExpressBet.getBetStatus()).isFalse();

        // Verify individual bet results
        assertThat(ordinaryBetRepo.findById(bet1.getId()).get().getWinningBet()).isTrue();
        assertThat(ordinaryBetRepo.findById(bet2.getId()).get().getWinningBet()).isFalse();
    }

    @Test
    @DisplayName("Should handle double chance bets correctly")
    void shouldHandleDoubleChanceBetsCorrectly() {
        // Given - Game ended 2-1 for home team
        FullBet bet1X = createFullBet(100.0, 1.5);
        FullBet bet12 = createFullBet(100.0, 1.8);
        FullBet bet2X = createFullBet(100.0, 2.0);

        createOrdinaryBet(testGame, bet1X, "1X", 1.5); // Should win (home win or draw)
        createOrdinaryBet(testGame, bet12, "12", 1.8); // Should win (any team wins)
        createOrdinaryBet(testGame, bet2X, "2X", 2.0); // Should lose (away win or draw)

        // When
        BetCalculationResultDTO result = betResultService.processGameResult(testGame.getId());

        // Then
        assertThat(result.getWinningBets()).isEqualTo(2);
        assertThat(result.getLosingBets()).isEqualTo(1);
        assertThat(result.getTotalPayouts()).isEqualTo(330.0); // 150 + 180
    }

    @Test
    @DisplayName("Should throw exception for non-existent game")
    void shouldThrowExceptionForNonExistentGame() {
        // When & Then
        assertThatThrownBy(() -> betResultService.processGameResult(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Game not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception for unfinished game")
    void shouldThrowExceptionForUnfinishedGame() {
        // Given
        Game unfinishedGame = new Game();
        unfinishedGame.setGameEnded(false);
        unfinishedGame = gameRepo.save(unfinishedGame);

        // When & Then
        Game finalUnfinishedGame = unfinishedGame;
        assertThatThrownBy(() -> betResultService.processGameResult(finalUnfinishedGame.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot process bets for unfinished game");
    }

    @Test
    @DisplayName("Should cancel game and refund single bets")
    void shouldCancelGameAndRefundSingleBets() {
        // Given
        Game activeGame = new Game();
        activeGame.setGameEnded(false);
        activeGame = gameRepo.save(activeGame);

        FullBet singleBet = createFullBet(200.0, 2.0);
        createOrdinaryBet(activeGame, singleBet, "Win1", 2.0);

        double initialBalance = testPlayer.getBalance();

        // When
        RefundResultDTO result = betResultService.cancelGame(activeGame.getId());

        // Then
        assertThat(result.getTotalBetsRefunded()).isEqualTo(1);
        assertThat(result.getRefundedBets()).isEqualTo(1);
        assertThat(result.getTotalRefundAmount()).isEqualTo(200.0);

        // Verify game marked as ended
        Game updatedGame = gameRepo.findById(activeGame.getId()).get();
        assertThat(updatedGame.getGameEnded()).isTrue();

        // Verify full refund
        FullBet updatedBet = fullBetRepo.findById(singleBet.getId()).get();
        assertThat(updatedBet.getFinalBetPayout()).isEqualTo(200.0);
        assertThat(updatedBet.getBetStatus()).isNull(); // null indicates refund

        // Verify balance refunded
        Player updatedPlayer = playerRepo.findById(testPlayer.getId()).get();
        assertThat(updatedPlayer.getBalance()).isEqualTo(initialBalance + 200.0);
    }

    @Test
    @DisplayName("Should handle extra time and penalty scores")
    void shouldHandleExtraTimeAndPenaltyScores() {
        // Given - Game with extra time and penalties
        testGame.setScoreTeamHome(1);
        testGame.setScoreTeamAway(1);
        testGame.setExtraTimeHomeScore(0);
        testGame.setExtraTimeAwayScore(1);
        testGame.setPenaltyHomeScore(4);
        testGame.setPenaltyAwayScore(3);
        testGame = gameRepo.save(testGame);

        FullBet regularTimeBet = createFullBet(100.0, 3.0);
        FullBet fullMatchBet = createFullBet(100.0, 2.0);

        createOrdinaryBet(testGame, regularTimeBet, "Draw", 3.0); // Regular time draw
        createOrdinaryBet(testGame, fullMatchBet, "Win of 1 team in match", 2.0); // Home wins overall

        // When
        BetCalculationResultDTO result = betResultService.processGameResult(testGame.getId());

        // Then
        assertThat(result.getWinningBets()).isEqualTo(2);
        assertThat(result.getTotalPayouts()).isEqualTo(500.0); // 300 + 200
    }

    // Helper methods
    private FullBet createFullBet(double amount, double coefficient) {
        FullBet fullBet = new FullBet();
        fullBet.setPlayer(testPlayer);
        fullBet.setBetAmount(amount);
        fullBet.setFinalCoefficient(coefficient);
        return fullBetRepo.save(fullBet);
    }

    private OrdinaryBet createOrdinaryBet(Game game, FullBet fullBet, String outcome, double coefficient) {
        OrdinaryBet bet = new OrdinaryBet();
        bet.setGame(game);
        bet.setFullBet(fullBet);
        bet.setOutcomeOfTheGame(outcome);
        bet.setCoefficient(coefficient);

        // Update the fullBet's bets list
        if (fullBet.getBets() == null) {
            fullBet.setBets(Arrays.asList(bet));
        } else {
            fullBet.getBets().add(bet);
        }

        return ordinaryBetRepo.save(bet);
    }
}