package com.sobolbetbackend.backendprojektbk1.controller.Admin;

import com.sobolbetbackend.backendprojektbk1.dto.Admin.listOfUsers.UserEDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Admin.listOfUsers.UserInfoDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration.WorkerCredentialDTO;
import com.sobolbetbackend.backendprojektbk1.service.adminServices.usersServices.AdminUserEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AllUsersController {
    private final AdminUserEService adminUserEService;

    @Autowired
    public AllUsersController(AdminUserEService adminUserEService) {
        this.adminUserEService = adminUserEService;
    }

    @GetMapping("/list/users")
    public ResponseEntity<List<UserEDTO>> getAllUsers(){
        return ResponseEntity.ok(adminUserEService.getAllUsers());
    }

    @GetMapping("/list/users/{role}")
    public ResponseEntity<List<UserEDTO>> getUsersByRole(@PathVariable String role){
        return ResponseEntity.ok(adminUserEService.getUsersByRole(role));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        adminUserEService.deleteUser(id);
        return ResponseEntity.ok().body("{\"message\": \"User with id "+id+" was successfully deleted\"}");
    }

    @GetMapping("/user/info/{id}")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable Long id){
        return ResponseEntity.ok(adminUserEService.getUserInfo(id));
    }
}
