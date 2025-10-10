package com.sobolbetbackend.backendprojektbk1.service.betServices;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.BetCalculationResultDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.RefundResultDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.FullBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.OrdinaryBet;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.FullBetRepo;
import com.sobolbetbackend.backendprojektbk1.repository.placeBet.OrdinaryBetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for processing sports betting results after game completion.
 * This service handles the calculation of individual bet outcomes, determines express bet winners,
 * processes player payouts, and maintains transaction integrity. It supports various bet types including
 * regular time outcomes, double chance bets, and full match results with extra time and penalties.
 * All operations are performed within database transactions to ensure data consistency.
 * If any error occurs during processing, all changes are rolled back.
 */
@Service
@Transactional
public class BetResultService {

    private final GameRepo gameRepo;
    private final OrdinaryBetRepo ordinaryBetRepo;
    private final FullBetRepo fullBetRepo;

    /**
     * Constructs a new BetResultService with required repositories.
     *
     * @param gameRepo repository for game data access
     * @param ordinaryBetRepo repository for individual bet management
     * @param fullBetRepo repository for express bet management
     */
    @Autowired
    public BetResultService(GameRepo gameRepo, OrdinaryBetRepo ordinaryBetRepo, FullBetRepo fullBetRepo) {
        this.gameRepo = gameRepo;
        this.ordinaryBetRepo = ordinaryBetRepo;
        this.fullBetRepo = fullBetRepo;
    }

    /**
     * Processes all betting results for a completed game and handles player payouts.
     * This method performs the complete bet processing workflow:
     * 1. Validates that the game has finished
     * 2. Calculates outcomes for all individual bets on the game
     * 3. Determines winners for express bets containing processed individual bets
     * 4. Processes payouts by updating player balances
     * 5. Saves all changes to the database
     * 6. Returns comprehensive processing statistics
     * Express bets require all individual bets to win for the entire express to win.
     * Payouts are calculated as: betAmount × finalCoefficient
     *
     * @param gameId the unique identifier of the completed game
     * @return BetCalculationResultDTO containing processing statistics including
     *         total payouts, bookmaker profit, and any processing errors
     * @throws RuntimeException if no game is found with the specified ID
     * @throws IllegalStateException if the game is not marked as finished
     */
    @Transactional
    public BetCalculationResultDTO processGameResult(Long gameId){
        BetCalculationResultDTO result = new BetCalculationResultDTO();

        // 1. Get game and verify it has finished
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        if (!game.getGameEnded()) {
            throw new IllegalStateException("Cannot process bets for unfinished game");
        }

        // 2. Find all ordinary bets for this game
        List<OrdinaryBet> ordinaryBets = ordinaryBetRepo.findByGameId(gameId);
        result.setTotalBetsProcessed(ordinaryBets.size());

        // 3. Calculate result for each ordinary bet
        for (OrdinaryBet bet : ordinaryBets) {
            try {
                boolean won = calculateSingleBetResult(bet, game);
                bet.setWinningBet(won);

                if (won) {
                    result.incrementWinningBets();
                } else {
                    result.incrementLosingBets();
                }

            } catch (Exception e) {
                result.addError("Error processing bet ID " + bet.getId() + ": " + e.getMessage());
            }
        }

        // 4. Get all unique full bets (express bets)
        Set<FullBet> fullBetsToProcess = ordinaryBets.stream()
                .map(OrdinaryBet::getFullBet)
                .filter(fullBet -> fullBet.getFinalBetPayout() == null) // Only unprocessed bets
                .collect(Collectors.toSet());

        // 5. Calculate express bets and process payouts
        double totalPayouts = 0.0;
        double totalBetAmounts = 0.0;

        for (FullBet fullBet : fullBetsToProcess) {
            try {
                totalBetAmounts += fullBet.getBetAmount();

                if (isFullBetWon(fullBet)) {
                    // Won - process payout
                    double payout = fullBet.getBetAmount() * fullBet.getFinalCoefficient();
                    fullBet.setFinalBetPayout(payout);
                    fullBet.setBetStatus(true);
                    fullBet.getPlayer().updateBalance(payout);
                    totalPayouts += payout;

                } else {
                    // Check if express is ready for processing
                    boolean allGamesFinished = fullBet.getBets().stream()
                            .allMatch(bet -> bet.getGame().getGameEnded());

                    if (allGamesFinished) {
                        // Express lost (all games finished but not won)
                        fullBet.setFinalBetPayout(0.0);
                        fullBet.setBetStatus(false);
                    } else {
                        // Express not ready - don't process y
                        continue; // Skip processing
                    }
                }

            } catch (Exception e) {
                result.addError("Error processing full bet ID " + fullBet.getId() + ": " + e.getMessage());
            }
        }

        // 6. Save all changes
        ordinaryBetRepo.saveAll(ordinaryBets);
        fullBetRepo.saveAll(fullBetsToProcess);

        // 7. Fill in statistics
        result.setTotalPayouts(totalPayouts);
        result.setBookmakerProfit(totalBetAmounts - totalPayouts);

        return result;
    }

    /**
     * Determines the outcome of an individual bet based on game results.
     * Supports various bet types:
     * - Regular time outcomes: Win1, Draw, Win2, 1X, 12, 2X
     * - Full match outcomes: Win of 1/2 team in match (including extra time and penalties)
     * Regular time bets only consider the main game score, while full match bets
     * include extra time and penalty scores in the calculation.
     *
     * @param bet the individual bet to evaluate
     * @param game the completed game with final scores
     * @return true if the bet won, false if the bet lost
     * @throws IllegalStateException if game scores are not set
     * @throws IllegalArgumentException if the bet outcome type is not supported
     */
    private boolean calculateSingleBetResult(OrdinaryBet bet, Game game) {
        String outcome = bet.getOutcomeOfTheGame();
        Integer homeScore = game.getScoreTeamHome();
        Integer awayScore = game.getScoreTeamAway();
        Integer extraTimeHome = game.getExtraTimeHomeScore();
        Integer extraTimeAway = game.getExtraTimeAwayScore();
        Integer penaltyHome = game.getPenaltyHomeScore();
        Integer penaltyAway = game.getPenaltyAwayScore();

        if (homeScore == null || awayScore == null) {
            throw new IllegalStateException("Game scores not set");
        }

        // Total score including extra time and penalties
        int totalHomeScore = homeScore +
                (extraTimeHome != null ? extraTimeHome : 0) +
                (penaltyHome != null ? penaltyHome : 0);
        int totalAwayScore = awayScore +
                (extraTimeAway != null ? extraTimeAway : 0) +
                (penaltyAway != null ? penaltyAway : 0);

        return switch (outcome) {
            // Regular time outcomes (main time only)
            case "Win1" -> homeScore > awayScore;
            case "Draw" -> homeScore.equals(awayScore);
            case "Win2" -> awayScore > homeScore;

            // Double chance (regular time)
            case "1X" -> // Home team win or draw
                    homeScore >= awayScore;
            case "12" -> // Any team wins (no draw)
                    !homeScore.equals(awayScore);
            case "2X" -> // Away team win or draw
                    awayScore >= homeScore;

            // Full match outcomes (including extra time and penalties)
            case "Win of 1 team in match" -> totalHomeScore > totalAwayScore;
            case "Win of 2 team in match" -> totalAwayScore > totalHomeScore;
            default -> throw new IllegalArgumentException("Unknown bet outcome: " + outcome);
        };
    }

    /**
     * Determines whether an express bet (combination bet) is won.
     * An express bet is considered won only if:
     * - All games included in the express bet have finished
     * - All individual bets within the express have won
     * If any individual bet in the express loses, the entire express bet loses.
     * If any games are still in progress, the express is not ready for processing.
     *
     * @param fullBet the express bet to evaluate
     * @return true if the express bet won, false if it lost or is not ready for processing
     */
    private boolean isFullBetWon(FullBet fullBet) {
        List<OrdinaryBet> bets = fullBet.getBets();

        // Check that all games in the express have finished
        boolean allGamesFinished = bets.stream()
                .allMatch(bet -> bet.getGame().getGameEnded());

        if (!allGamesFinished) {
            return false; // Express is not ready yet
        }

        // In express, all bets must win
        return bets.stream()
                .allMatch(bet -> bet.getWinningBet() == Boolean.TRUE);
    }

    /**
     * Cancels a game and refunds all bets placed on it.
     * All players receive full refunds of their bet amounts.
     * This operation is typically used when a game is cancelled due to weather,
     * player injuries, or other unforeseen circumstances.
     *
     * @param gameId the unique identifier of the game to cancel
     * @return RefundResultDTO containing statistics about refunded bets
     * @throws RuntimeException if no game is found with the specified ID
     * @throws IllegalStateException if the game is already finished or bets are already processed
     */
    @Transactional
    public RefundResultDTO cancelGame(Long gameId) {
        RefundResultDTO result = new RefundResultDTO();

        // 1. Get game and validate it can be cancelled
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        if (game.getGameEnded()) {
            throw new IllegalStateException("Cannot cancel already finished game");
        }

        // 2. Mark game as cancelled/ended
        game.setGameEnded(true);

        // 3. Find all ordinary bets for this game
        List<OrdinaryBet> ordinaryBets = ordinaryBetRepo.findByGameId(gameId);
        result.setTotalBetsRefunded(ordinaryBets.size());

        // 4. Mark all ordinary bets as refunded
        for (OrdinaryBet bet : ordinaryBets) {
            bet.setWinningBet(null); // null = refund
        }

        // 5. Get all unique full bets that need processing
        Set<FullBet> fullBetsToProcess = ordinaryBets.stream()
                .map(OrdinaryBet::getFullBet)
                .filter(fullBet -> fullBet.getFinalBetPayout() == null)
                .collect(Collectors.toSet());

        double totalRefundAmount = 0.0;

        // 6. Process each full bet
        for (FullBet fullBet : fullBetsToProcess) {
            try {
                if (fullBet.getBets().size() == 1) {
                    // Single bet - full refund with coefficient 1.0
                    fullBet.setFinalCoefficient(1.0);
                    fullBet.setFinalBetPayout(fullBet.getBetAmount());
                    fullBet.setBetStatus(null); // null indicates refund

                    fullBet.getPlayer().updateBalance(fullBet.getBetAmount());
                    totalRefundAmount += fullBet.getBetAmount();

                } else {
                    // Express bet - find the cancelled bet and its coefficient
                    OrdinaryBet cancelledBet = ordinaryBets.stream()
                            .filter(bet -> bet.getFullBet().getId().equals(fullBet.getId()))
                            .findFirst()
                            .orElseThrow();

                    // Divide final coefficient by cancelled bet's coefficient
                    double adjustedCoefficient = fullBet.getFinalCoefficient() / cancelledBet.getCoefficient();
                    fullBet.setFinalCoefficient(adjustedCoefficient);

                    // Check if all other games in express are finished
                    boolean allOtherGamesFinished = fullBet.getBets().stream()
                            .filter(bet -> !bet.getGame().getId().equals(gameId)) // Exclude cancelled game
                            .allMatch(bet -> bet.getGame().getGameEnded());

                    if (allOtherGamesFinished) {
                        // Express is ready for final calculation
                        boolean hasLosingBets = fullBet.getBets().stream()
                                .filter(bet -> !bet.getGame().getId().equals(gameId)) // Exclude cancelled game
                                .anyMatch(bet -> bet.getWinningBet() == Boolean.FALSE);

                        if (hasLosingBets) {
                            // Express lost
                            fullBet.setFinalBetPayout(0.0);
                            fullBet.setBetStatus(false);
                        } else {
                            // Express won with adjusted coefficient
                            double payout = fullBet.getBetAmount() * adjustedCoefficient;
                            fullBet.setFinalBetPayout(payout);
                            fullBet.setBetStatus(true);

                            fullBet.getPlayer().updateBalance(payout);
                            totalRefundAmount += payout;
                        }
                    }

                }

                result.incrementRefundedBets();

            } catch (Exception e) {
                result.addError("Error processing bet ID " + fullBet.getId() + ": " + e.getMessage());
            }
        }

        // 7. Save all changes
        gameRepo.save(game);
        ordinaryBetRepo.saveAll(ordinaryBets);
        fullBetRepo.saveAll(fullBetsToProcess);

        result.setTotalRefundAmount(totalRefundAmount);
        return result;
    }
}