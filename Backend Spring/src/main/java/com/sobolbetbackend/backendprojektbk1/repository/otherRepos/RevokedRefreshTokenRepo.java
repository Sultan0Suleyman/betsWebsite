package com.sobolbetbackend.backendprojektbk1.repository.otherRepos;

import com.sobolbetbackend.backendprojektbk1.entity.other.RevokedRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RevokedRefreshTokenRepo extends CrudRepository<RevokedRefreshToken,String> {
    boolean existsByToken(String token);
}
