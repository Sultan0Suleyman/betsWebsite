package com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeagueRepo extends CrudRepository<League,String> {
    List<League> findBySportAndCountry(Sport sport, Country country);
    List<League> findBySport(Sport sport);
}
