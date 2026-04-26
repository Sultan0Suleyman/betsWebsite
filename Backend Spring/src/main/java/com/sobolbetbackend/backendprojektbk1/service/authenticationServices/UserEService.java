package com.sobolbetbackend.backendprojektbk1.service.authenticationServices;

import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserEService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserEService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserE user;
        if(username.contains("@")) {
            user = userRepo.findByEmail(username);
        }else if(StringUtils.isNumeric(username)){
            user = userRepo.findById(Long.parseLong(username)).orElse(null);
        }else{
            throw new BadCredentialsException("Invalid username format: " + username);
        }
        if(user==null) throw new UsernameNotFoundException("User not found with username: " + username);

        return User.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .authorities( user.getAuthorities())
                .build();
    }
}
