package com.sobolbetbackend.backendprojektbk1.service.adminServices;

import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.other.Role;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import com.sobolbetbackend.backendprojektbk1.repository.otherRepos.RoleRepo;
import com.sobolbetbackend.backendprojektbk1.service.adminServices.passwordServices.AdminPasswordService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Service
public class AdminInitializer {

    private final UserRepo userRepo;
    private final AdminPasswordService adminPasswordService;
    private final RoleRepo roleRepo;

    @Autowired
    public AdminInitializer(UserRepo userRepo,
                            AdminPasswordService adminPasswordService, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.adminPasswordService = adminPasswordService;
        this.roleRepo = roleRepo;
    }

    @PostConstruct
    public void initAdmin() {
        String adminEmail = "admin@system.com";

        if (!userRepo.existsByEmail(adminEmail)) {

            UserE admin = new UserE();
            admin.setName("Admin");
            admin.setSurname("Admin");
            admin.setEmail(adminEmail);
            admin.setNumberOfPassport("ADMIN");
            admin.setPassportIssueDate(LocalDate.now());
            admin.setPassportIssuingAuthority("SYSTEM");

            String rawPassword = adminPasswordService.generateRandomPassword(32);
            String encodedPassword = adminPasswordService.encodePassword(rawPassword);

            admin.setPassword(encodedPassword);
            Role adminRole = roleRepo.findByName("ROLE_MAIN_ADMIN");
            if (adminRole == null) {
                throw new IllegalStateException("Role ROLE_MAIN_ADMIN not found");
            }
            admin.setRoles(Collections.singletonList(adminRole));

            userRepo.save(admin);

            System.out.println("ADMIN CREATED. PASSWORD: " + rawPassword);
        }
    }
}
