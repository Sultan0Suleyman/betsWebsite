package com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Sport;
import org.springframework.data.repository.CrudRepository;

public interface SportRepo extends CrudRepository<Sport, String> {
}
