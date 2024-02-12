package com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos;

import com.sobolbetbackend.backendprojektbk1.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PlayerRepo extends CrudRepository<Player,Long> {
    Player findByUserId(Long user_id);
}
