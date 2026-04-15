package com.sobolbetbackend.backendprojektbk1.service.matchSettlementServices;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.BetCalculationResultDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.linemakerBetsStats.RefundResultDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsSettlement.FinishGameRequestDTO;
import com.sobolbetbackend.backendprojektbk1.dto.betsSettlement.SettlementMatchDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Service responsible for handling match settlement workflow.
 * Provides functionality to:
 * - Finish a match and settle all related bets
 * - Cancel a match and refund all bets
 * - Save match score without finishing the match
 * - Retrieve matches available for settlement
 * All operations are transactional to ensure data consistency.
 */
@Service
@Transactional
public class MatchSettlementService {

    private final GameRepo gameRepo;
    private final BetResultService betResultService;

    public MatchSettlementService(GameRepo gameRepo, BetResultService betResultService) {
        this.gameRepo = gameRepo;
        this.betResultService = betResultService;
    }

    /**
     * Finishes a match, sets final scores and processes all bets.
     * Steps:
     * - Validates match state
     * - Saves final scores (including extra time and penalties)
     * - Marks match as ended
     * - Processes all bets for the match
     * - Marks results as processed
     *
     * @param gameId match identifier
     * @param request final score data
     * @return statistics about processed bets
     */
    public BetCalculationResultDTO finishGameAndSettle(Long gameId, FinishGameRequestDTO request) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        if (Boolean.TRUE.equals(game.getResultsProcessed())) {
            throw new IllegalStateException("Match already settled");
        }

        if (Boolean.TRUE.equals(game.getGameEnded())) {
            throw new IllegalStateException("Match already finished");
        }

        if (request.getHomeScore() == null || request.getAwayScore() == null) {
            throw new IllegalArgumentException("Home score and away score are required");
        }

        game.setScoreTeamHome(request.getHomeScore());
        game.setScoreTeamAway(request.getAwayScore());
        game.setExtraTimeHomeScore(request.getExtraTimeHomeScore());
        game.setExtraTimeAwayScore(request.getExtraTimeAwayScore());
        game.setPenaltyHomeScore(request.getPenaltyHomeScore());
        game.setPenaltyAwayScore(request.getPenaltyAwayScore());

        game.setGameEnded(true);
        game.setGameInLive(false);
        game.setGamePosted(false);

        gameRepo.save(game);

        BetCalculationResultDTO result = betResultService.processGameResult(gameId);

        game.setResultsProcessed(true);
        gameRepo.save(game);

        return result;
    }

    /**
     * Retrieves all matches that are not yet settled.
     * Matches are sorted by date in ascending order.
     *
     * @return list of matches ready for settlement
     */
    public List<SettlementMatchDTO> getMatchesForSettlement() {
        List<Game> games = (List<Game>) gameRepo.findAll();

        return games.stream()
                .filter(game -> !Boolean.TRUE.equals(game.getResultsProcessed()))
                .sorted(Comparator.comparing(Game::getDateOfMatch))
                .map(this::mapToSettlementMatchDTO)
                .toList();
    }

    /**
     * Retrieves detailed information for a specific match.
     *
     * @param gameId match identifier
     * @return match data for settlement view
     */
    public SettlementMatchDTO getSettlementMatch(Long gameId) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        return mapToSettlementMatchDTO(game);
    }

    /**
     * Converts Game entity into SettlementMatchDTO.
     *
     * @param game game entity
     * @return mapped DTO
     */
    private SettlementMatchDTO mapToSettlementMatchDTO(Game game) {
        return SettlementMatchDTO.builder()
                .id(game.getId())
                .sport(game.getSport().getName_en())
                .country(game.getCountry() != null ? game.getCountry().getName() : "N/A")
                .league(game.getLeague().getName())
                .homeTeam(game.getTeamHome().getName_en())
                .awayTeam(game.getTeamAway().getName_en())
                .dateOfMatch(game.getDateOfMatch())
                .scoreHome(game.getScoreTeamHome())
                .scoreAway(game.getScoreTeamAway())
                .extraTimeHomeScore(game.getExtraTimeHomeScore())
                .extraTimeAwayScore(game.getExtraTimeAwayScore())
                .penaltyHomeScore(game.getPenaltyHomeScore())
                .penaltyAwayScore(game.getPenaltyAwayScore())
                .gameEnded(game.getGameEnded())
                .resultsProcessed(game.getResultsProcessed())
                .build();
    }

    /**
     * Cancels a match and refunds all related bets.
     *
     * @param gameId match identifier
     * @return refund statistics
     */
    public RefundResultDTO cancelGameAndRefund(Long gameId) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        if (Boolean.TRUE.equals(game.getResultsProcessed())) {
            throw new IllegalStateException("Match already settled");
        }

        RefundResultDTO result = betResultService.cancelGame(gameId);

        game.setResultsProcessed(true);
        game.setGamePosted(false);
        game.setGameInLive(false);
        gameRepo.save(game);

        return result;
    }

    /**
     * Saves match score without finishing the match.
     * Used for live updates or intermediate score saving.
     *
     * @param gameId match identifier
     * @param request score data
     * @return updated match data
     */
    public SettlementMatchDTO saveScore(Long gameId, FinishGameRequestDTO request) {
        Game game = gameRepo.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        if (Boolean.TRUE.equals(game.getResultsProcessed())) {
            throw new IllegalStateException("Match already settled");
        }

        if (request.getHomeScore() == null || request.getAwayScore() == null) {
            throw new IllegalArgumentException("Home score and away score are required");
        }

        if (request.getHomeScore() < 0 || request.getAwayScore() < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }

        game.setScoreTeamHome(request.getHomeScore());
        game.setScoreTeamAway(request.getAwayScore());
        game.setExtraTimeHomeScore(request.getExtraTimeHomeScore());
        game.setExtraTimeAwayScore(request.getExtraTimeAwayScore());
        game.setPenaltyHomeScore(request.getPenaltyHomeScore());
        game.setPenaltyAwayScore(request.getPenaltyAwayScore());

        gameRepo.save(game);

        return mapToSettlementMatchDTO(game);
    }
}
