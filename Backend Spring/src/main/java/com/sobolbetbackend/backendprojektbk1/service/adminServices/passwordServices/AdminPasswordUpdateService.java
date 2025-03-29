package com.sobolbetbackend.backendprojektbk1.service.adminServices.passwordServices;

import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class AdminPasswordUpdateService {
    private final UserRepo userRepo;
    private final AdminPasswordService adminPasswordService;

    @Autowired
    public AdminPasswordUpdateService(UserRepo userRepo, AdminPasswordService adminPasswordService) {
        this.userRepo = userRepo;
        this.adminPasswordService = adminPasswordService;
    }

    @Scheduled(fixedRate = 30*60*1000)
    public void updateAdminPassword(){
        if(userRepo.existsById(1234567931L)){
            UserE admin = userRepo.findById(1234567931);

            String newRawPassword = adminPasswordService.generateRandomPassword(32);
            String encodedPassword = adminPasswordService.encodePassword(newRawPassword);

            writePasswordToFile(newRawPassword);

            admin.setPassword(encodedPassword);
            userRepo.save(admin);
        }else{
            throw new UsernameNotFoundException("Admin not found");
        }
    }

    private void writePasswordToFile(String password){
        try(FileWriter writer = new FileWriter("C:\\Users\\mrlen\\Desktop\\Admin_password.txt")){
            writer.write(password);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
