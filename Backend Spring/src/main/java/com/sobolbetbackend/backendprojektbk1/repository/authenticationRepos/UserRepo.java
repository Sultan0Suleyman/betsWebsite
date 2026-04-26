package com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos;

import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<UserE,Long> {
    UserE findByEmail(String email);
    UserE findByNumberOfPassport(String numberOfPassport);
    boolean existsByEmail(String email);
}
