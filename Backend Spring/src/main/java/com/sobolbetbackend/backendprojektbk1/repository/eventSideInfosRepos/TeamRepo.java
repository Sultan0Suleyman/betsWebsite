package com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.League;
import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepo extends CrudRepository<Team, String> {
    List<Team> findByLeague(League league);
}
