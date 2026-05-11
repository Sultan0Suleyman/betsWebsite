package com.sobolbetbackend.backendprojektbk1.service.linemaker;

import com.sobolbetbackend.backendprojektbk1.dto.Linemaker.unpublishedMatches.LinemakerMatchInfoDTO;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Team;
import com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos.GameRepo;
import com.sobolbetbackend.backendprojektbk1.service.linemakerServices.LineMatchesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineMatchesServiceTest {

    @Mock
    private GameRepo gameRepo;

    @InjectMocks
    private LineMatchesService lineMatchesService;

    @Test
    void getLineMatches_WhenPostedGamesExist_ShouldReturnMappedDtos() {
        LocalDateTime dateOfMatch = LocalDateTime.of(2026, 5, 11, 18, 30);
        Game game = createGame(1L, "Football", "England", "Premier League",
                "Arsenal", "Chelsea", dateOfMatch, Game.Status.IN_PROGRESS, "John Doe");
        when(gameRepo.findByIsGamePosted(true)).thenReturn(List.of(game));

        List<LinemakerMatchInfoDTO> result = lineMatchesService.getLineMatches();

        assertEquals(1, result.size());
        LinemakerMatchInfoDTO dto = result.getFirst();
        assertEquals("1", dto.getId());
        assertEquals("Football", dto.getSport());
        assertEquals("England", dto.getCountry());
        assertEquals("Premier League", dto.getLeague());
        assertEquals("Arsenal", dto.getTeamHome());
        assertEquals("Chelsea", dto.getTeamAway());
        assertEquals(dateOfMatch.toString(), dto.getDateOfMatch());
        assertEquals("IN_PROGRESS", dto.getStatus());
        assertEquals("John Doe", dto.getLinemakersName());
        verify(gameRepo).findByIsGamePosted(true);
    }

    @Test
    void getLineMatches_WhenGameHasNoCountry_ShouldReturnDtoWithNullCountry() {
        Game game = createGame(2L, "Basketball", null, "EuroLeague",
                "Real Madrid", "Barcelona", LocalDateTime.of(2026, 6, 1, 20, 0),
                Game.Status.PENDING, "Jane Smith");
        when(gameRepo.findByIsGamePosted(true)).thenReturn(List.of(game));

        List<LinemakerMatchInfoDTO> result = lineMatchesService.getLineMatches();

        assertEquals(1, result.size());
        assertNull(result.getFirst().getCountry());
        assertEquals("Basketball", result.getFirst().getSport());
        assertEquals("EuroLeague", result.getFirst().getLeague());
    }

    @Test
    void getLineMatches_WhenNoPostedGamesExist_ShouldReturnEmptyList() {
        when(gameRepo.findByIsGamePosted(true)).thenReturn(Collections.emptyList());

        List<LinemakerMatchInfoDTO> result = lineMatchesService.getLineMatches();

        assertTrue(result.isEmpty());
        verify(gameRepo).findByIsGamePosted(true);
    }

    @Test
    void getLineMatches_WhenMultiplePostedGamesExist_ShouldPreserveRepositoryOrder() {
        Game firstGame = createGame(1L, "Football", "England", "Premier League",
                "Arsenal", "Chelsea", LocalDateTime.of(2026, 5, 11, 18, 30),
                Game.Status.IN_PROGRESS, "John Doe");
        Game secondGame = createGame(2L, "Basketball", "Spain", "ACB",
                "Real Madrid", "Barcelona", LocalDateTime.of(2026, 5, 12, 19, 0),
                Game.Status.DONE, "Jane Smith");
        when(gameRepo.findByIsGamePosted(true)).thenReturn(List.of(firstGame, secondGame));

        List<LinemakerMatchInfoDTO> result = lineMatchesService.getLineMatches();

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());
        assertEquals("DONE", result.get(1).getStatus());
        assertEquals("Jane Smith", result.get(1).getLinemakersName());
    }

    private Game createGame(Long id, String sportName, String countryName, String leagueName,
                            String teamHomeName, String teamAwayName, LocalDateTime dateOfMatch,
                            Game.Status status, String linemakersName) {
        Sport sport = new Sport(sportName);
        Country country = countryName != null ? new Country(countryName) : null;
        League league = new League(leagueName, country, sport);
        Team teamHome = new Team(teamHomeName, league, sport, country);
        Team teamAway = new Team(teamAwayName, league, sport, country);

        Game game = new Game();
        game.setId(id);
        game.setSport(sport);
        game.setCountry(country);
        game.setLeague(league);
        game.setTeamHome(teamHome);
        game.setTeamAway(teamAway);
        game.setDateOfMatch(dateOfMatch);
        game.setStatus(status);
        game.setLinemakersName(linemakersName);
        return game;
    }
}
