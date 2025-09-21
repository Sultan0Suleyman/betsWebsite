package com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.*;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepo extends CrudRepository<Game, Long> {
    Iterable<Game> findAllBySport(Sport sport);
    Iterable<Game> findAllByCountryAndSport(Country country, Sport sport);
    Iterable<Game> findAllByCountryAndSportAndLeague(Country country, Sport sport, League league);
    List<Game> findByTeamHomeAndTeamAwayAndDateOfMatch(
            Team teamHome, Team teamAway, LocalDateTime dateOfMatch);
}

