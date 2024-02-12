package com.sobolbetbackend.backendprojektbk1.repository.mainEventsRepos;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Game;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import org.springframework.data.repository.CrudRepository;

public interface GameRepo extends CrudRepository<Game, Long> {
    Iterable<Game> findAllByCountry(Country country);
    Iterable<Game> findAllBySport(Sport sport);
    Iterable<Game> findAllByCountryAndSport(Country country, Sport sport);
    Iterable<Game> findAllByCountryAndSportAndLeague(Country country, Sport sport, League league);
}

