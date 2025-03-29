package com.sobolbetbackend.backendprojektbk1.service.playerRegistrationServices;

import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.other.Role;
import com.sobolbetbackend.backendprojektbk1.exception.EmailAlreadyExistsException;
import com.sobolbetbackend.backendprojektbk1.exception.UserAlreadyRegisteredException;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import com.sobolbetbackend.backendprojektbk1.repository.otherRepos.RoleRepo;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class PlayerService{
    private final PlayerRepo playerRepo;
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public PlayerService(PlayerRepo playerRepo, UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder) {
        this.playerRepo = playerRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
    }

    @Transactional
    public void registration(UserE user) throws EmailAlreadyExistsException,UserAlreadyRegisteredException{
        if(userRepo.findByEmail(user.getEmail())!=null){
            throw new EmailAlreadyExistsException("User with this email already exists!");
        }if(userRepo.findByNumberOfPassport(user.getNumberOfPassport())!=null){
            throw new UserAlreadyRegisteredException("You already have an account on our service");
        }
        Role rolePlayer = roleRepo.findByName("ROLE_PLAYER");

        if (rolePlayer == null) {
            // Handle the case when the role doesn't exist
            throw new RuntimeException("ROLE_PLAYER not found in the database");
        }else{
            System.out.println(rolePlayer);
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set roles to the user
        user.setRoles(Collections.singletonList(rolePlayer));

        // Save the user entity
        user = userRepo.save(user);

        // Now the user entity is managed by the persistence context
        // You can proceed with other operations, if any
        Player player = new Player();
        player.setUser(user);
        playerRepo.save(player);
    }


}
