package com.sobolbetbackend.backendprojektbk1.repository.eventSideInfosRepos;

import com.sobolbetbackend.backendprojektbk1.entity.events.sideInfo.Country;
import org.springframework.data.repository.CrudRepository;

public interface CountryRepo extends CrudRepository<Country,String> {
}
