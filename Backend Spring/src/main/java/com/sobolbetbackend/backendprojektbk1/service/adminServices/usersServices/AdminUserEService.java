package com.sobolbetbackend.backendprojektbk1.service.adminServices.usersServices;

import com.sobolbetbackend.backendprojektbk1.dto.Admin.UserEDTO;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.other.Role;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import com.sobolbetbackend.backendprojektbk1.repository.otherRepos.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdminUserEService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;

    @Autowired
    public AdminUserEService(UserRepo userRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    public List<UserEDTO> getAllUsers(){
        List<UserEDTO> allUsersList = new ArrayList<>();
        String roleName;
        for(UserE userE : userRepo.findAll()){
            Role role = userE.getRoles().get(0);
            if(Objects.equals(role.getName(), "ROLE_MAIN_ADMIN")){
                continue;
            }
            roleName = switch (role.getName()) {
                case "ROLE_PLAYER" -> "Player";
                case "ROLE_LINEMAKER" -> "Linemaker";
                case "ROLE_SUPPORT" -> "Support";
                default -> null;
            };
            allUsersList.add(new UserEDTO(userE.getId(),userE.getName(),userE.getSurname(),roleName));
        }
        return allUsersList;
    }

    public List<UserEDTO> getUsersByRole(String role){
        List<UserEDTO> usersList = new ArrayList<>();
        String roleName = switch (role) {
            case "ROLE_PLAYER" -> "Player";
            case "ROLE_LINEMAKER" -> "Linemaker";
            case "ROLE_SUPPORT" -> "Support";
            default -> null;
        };
        for(UserE userE : userRepo.findAll()){
            if(Objects.equals(userE.getRoles().get(0).getName(), role)){
                usersList.add(new UserEDTO(userE.getId(),userE.getName(),userE.getSurname(),roleName));
            }
        }
        return usersList;
    }

    public void deleteUser(Long id){
        userRepo.deleteById(id);
    }


}
