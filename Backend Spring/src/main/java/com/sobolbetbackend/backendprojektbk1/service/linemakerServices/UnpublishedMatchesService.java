package com.sobolbetbackend.backendprojektbk1.service.linemakerServices;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches.*;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEvent;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingEventStatus;
import com.sobolbetbackend.backendprojektbk1.entity.events.bet.BettingOdd;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.repository.Linemaker.BettingEventRepo;
import com.sobolbetbackend.backendprojektbk1.repository.Linemaker.BettingOddRepo;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service responsible for managing unpublished matches in the linemaker system.
 * This service handles operations related to unpublished matches including:
 * - Retrieving unpublished matches
 * - Updating match status and linemaker assignments
 * - Managing betting odds for matches
 * - Publishing and unpublishing matches
 * - Deleting unpublished matches
 * All transactional operations are properly annotated to ensure data consistency.
 *
 * @author SobolBet Backend Team
 * @since 1.0
 */
@Service
public class UnpublishedMatchesService {
    private final GameRepo gameRepo;
    private final UserRepo userRepo;
    private final BettingEventRepo bettingEventRepo;
    private final BettingOddRepo bettingOddRepo;

    /**
     * Constructs a new UnpublishedMatchesService with required repository dependencies.
     *
     * @param gameRepo the repository for Game entities
     * @param userRepo the repository for User entities
     * @param bettingEventRepo the repository for BettingEvent entities
     * @param bettingOddRepo the repository for BettingOdd entities
     */
    @Autowired
    public UnpublishedMatchesService(GameRepo gameRepo, UserRepo userRepo,
                                     BettingEventRepo bettingEventRepo, BettingOddRepo bettingOddRepo) {
        this.gameRepo = gameRepo;
        this.userRepo = userRepo;
        this.bettingEventRepo = bettingEventRepo;
        this.bettingOddRepo = bettingOddRepo;
    }

    /**
     * Retrieves all unpublished matches from the database.
     * This method fetches all games where {@code isGamePosted} is false and converts them
     * into DTOs containing essential match information including sport, league, teams,
     * match date, status, and assigned linemaker name.
     * Country information is handled safely - if a game has no associated country,
     * the country field in the DTO will be null.
     *
     * @return a list of {@link LinemakerMatchInfoDTO} objects representing all unpublished matches.
     *         Returns an empty list if no unpublished matches exist.
     */
    public List<LinemakerMatchInfoDTO> getUnpublishedMatches(){
        List<Game> unpublishedMatches = gameRepo.findByIsGamePosted(false);
        List<LinemakerMatchInfoDTO> linemakerMatchInfoDTOS = new ArrayList<>();
        for (Game game : unpublishedMatches) {
            // Check for null before calling getName()
            String countryName = game.getCountry() != null ? game.getCountry().getName() : null;

            linemakerMatchInfoDTOS.add(new LinemakerMatchInfoDTO(
                    game.getId().toString(),
                    game.getSport().getName_en(),
                    countryName,  // Can be null
                    game.getLeague().getName(),
                    game.getTeamHome().getName_en(),
                    game.getTeamAway().getName_en(),
                    game.getDateOfMatch().toString(),
                    game.getStatus().toString(),
                    game.getLinemakersName()
            ));
        }
        return linemakerMatchInfoDTOS;
    }

    /**
     * Retrieves the name and surname of a linemaker by their user ID.
     * This method is used to fetch linemaker information for display purposes
     * in the frontend application.
     *
     * @param id the user ID as a string, which will be parsed to a Long
     * @return a {@link LinemakersNameSurnameDTO} containing the linemaker's first and last name
     * @throws NumberFormatException if the provided ID cannot be parsed to a Long
     * @throws NullPointerException if no user is found with the given ID
     */
    public LinemakersNameSurnameDTO getLinemakersNameSurname(String id){
        UserE user = userRepo.findById(Long.parseLong(id));
        return new LinemakersNameSurnameDTO(user.getName(),user.getSurname());
    }

    /**
     * Updates the status and linemaker assignment for a specific match.
     * This transactional method allows linemakers to update their progress on a match
     * by changing the status (NONE, IN_PROGRESS, PENDING, DONE) and assigning their name.
     * The changes are immediately persisted to the database.
     *
     * @param updateMatchStatusDTO DTO containing the match ID, new status, and linemaker's name
     * @throws RuntimeException if no match is found with the specified ID
     */
    @Transactional
    public void updateMatchStatus(UpdateMatchStatusDTO updateMatchStatusDTO){
        Game match = gameRepo.findById(updateMatchStatusDTO.getMatchId()).orElse(null);
        if (match != null) {
            match.setStatus(Game.Status.valueOf(updateMatchStatusDTO.getStatus()));
            match.setLinemakersName(updateMatchStatusDTO.getLinemakersName());
            gameRepo.save(match);
        } else {
            throw new RuntimeException("Match with id " + updateMatchStatusDTO.getMatchId() + " not found");
        }
    }

    /**
     * Deletes an unpublished match from the database.
     * This transactional method permanently removes a match from the system.
     * It includes a safety check to prevent deletion of published matches,
     * ensuring data integrity and preventing accidental deletion of live matches.
     *
     * @param matchId the ID of the match to delete
     * @throws RuntimeException if no match is found with the specified ID
     * @throws RuntimeException if the match is already published (cannot delete published matches)
     */
    @Transactional
    public void deleteMatch(Long matchId) {
        Game match = gameRepo.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match with id " + matchId + " not found"));

        if (match.getGamePosted()) {
            throw new RuntimeException("Cannot delete published match");
        }

        gameRepo.delete(match);
    }

    /**
     * Retrieves detailed information about an unpublished match including its betting odds.
     * This method fetches comprehensive match details and any associated betting odds
     * that have been set up for the match. If no betting event exists for the match,
     * an empty odds map is returned.
     * The returned DTO includes:
     * - Match identification and basic info (sport, league, teams, date)
     * - Country information (can be null)
     * - All betting odds mapped by type (e.g., "win1", "draw", "win2")
     *
     * @param id the ID of the match to retrieve
     * @return an {@link UnpublishedMatchOddsDetailsDTO} containing match details and odds
     * @throws RuntimeException if no match is found with the specified ID
     */
    public UnpublishedMatchOddsDetailsDTO getUnpublishedMatchOddsDetails(Long id) {
        Game match = gameRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));

        UnpublishedMatchOddsDetailsDTO dto = new UnpublishedMatchOddsDetailsDTO();
        dto.setId(match.getId());
        dto.setSport(match.getSport().getName_en());
        dto.setCountry(match.getCountry() != null ? match.getCountry().getName() : null);
        dto.setLeague(match.getLeague().getName());
        dto.setTeamHome(match.getTeamHome().getName_en());
        dto.setTeamAway(match.getTeamAway().getName_en());
        dto.setDateOfMatch(match.getDateOfMatch().toString());

        BettingEvent bettingEvent = bettingEventRepo.findByGame(match).orElse(null);

        Map<String, Double> oddsMap = new HashMap<>();

        if (bettingEvent != null) {
            List<BettingOdd> odds = bettingOddRepo.findAllByBettingEvent(bettingEvent);
            for (BettingOdd odd : odds) {
                if (odd.getValue() != null) {
                    oddsMap.put(odd.getType(), odd.getValue());
                }
            }
        }

        dto.setOdds(oddsMap);
        return dto;
    }

    /**
     * Saves or updates betting odds for a specific match.
     * This transactional method handles the complete odds management workflow:
     * 1. Creates a new BettingEvent if one doesn't exist (with DRAFT status)
     * 2. Deletes all existing odds for the betting event
     * 3. Saves the new odds provided in the request
     * Null odd values are automatically skipped during the save process.
     * This allows partial odds updates without requiring all odd types.
     *
     * @param request DTO containing the match ID and a map of betting odds by type
     * @throws IllegalArgumentException if no game is found with the specified match ID
     */
    @Transactional
    public void saveOdds(SetOddsRequestDTO request) {
        Game game = gameRepo.findById(request.getMatchId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + request.getMatchId()));

        // If the game has no BettingEvent - create a new one
        BettingEvent bettingEvent = bettingEventRepo.findByGame(game)
                .orElseGet(() -> {
                    BettingEvent event = new BettingEvent();
                    event.setGame(game);
                    event.setStatus(BettingEventStatus.DRAFT);
                    return bettingEventRepo.save(event);
                });

        // Delete old odds before saving new ones (if updating)
        List<BettingOdd> existingOdds = bettingOddRepo.findAllByBettingEvent(bettingEvent);
        bettingOddRepo.deleteAll(existingOdds);

        // Save new odds
        for (Map.Entry<String, Double> entry : request.getOdds().entrySet()) {
            if (entry.getValue() == null) continue;

            BettingOdd odd = new BettingOdd();
            odd.setBettingEvent(bettingEvent);
            odd.setType(entry.getKey());
            odd.setValue(entry.getValue());
            bettingOddRepo.save(odd);
        }
    }

    /**
     * Publishes a match, making it visible to end users for betting.
     * This transactional method marks a match as published and updates the associated
     * betting event status to PUBLISHED. Once published, the match becomes available
     * on the public betting platform.
     * Prerequisites:
     * - The match must exist
     * - The match must have an associated BettingEvent
     *
     * @param id the ID of the match to publish
     * @throws RuntimeException if no match is found with the specified ID
     * @throws NullPointerException if the match has no associated BettingEvent
     */
    @Transactional
    public void publishMatch(Long id) {
        Game match = gameRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        match.setGamePosted(true);
        BettingEvent bettingEvent = bettingEventRepo.findByGame(match).orElse(null);
        Objects.requireNonNull(bettingEvent).setStatus(BettingEventStatus.PUBLISHED);
        gameRepo.save(match);
    }

    /**
     * Unpublishes a previously published match, removing it from public view.
     * This transactional method reverts a match to unpublished state and changes
     * the betting event status back to DRAFT. This is typically used when a match
     * needs to be corrected or temporarily removed from the betting platform.
     * Prerequisites:
     * - The match must exist
     * - The match must have an associated BettingEvent
     *
     * @param id the ID of the match to unpublish
     * @throws RuntimeException if no match is found with the specified ID
     * @throws NullPointerException if the match has no associated BettingEvent
     */
    @Transactional
    public void unPublishMatch(Long id) {
        Game match = gameRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        match.setGamePosted(false);
        BettingEvent bettingEvent = bettingEventRepo.findByGame(match).orElse(null);
        Objects.requireNonNull(bettingEvent).setStatus(BettingEventStatus.DRAFT);
        gameRepo.save(match);
    }
}