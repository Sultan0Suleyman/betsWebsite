package com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Team;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepo extends CrudRepository<Team, String> {
}
