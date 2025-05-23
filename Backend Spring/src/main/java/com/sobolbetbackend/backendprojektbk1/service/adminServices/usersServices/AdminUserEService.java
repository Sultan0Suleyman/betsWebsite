package com.sobolbetbackend.backendprojektbk1.service.adminServices.usersServices;

import com.sobolbetbackend.backendprojektbk1.dto.Admin.listOfUsers.UserEDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Admin.listOfUsers.UserInfoDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration.ContractDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration.WorkerCredentialDTO;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.common.Worker;
import com.sobolbetbackend.backendprojektbk1.entity.other.Role;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import com.sobolbetbackend.backendprojektbk1.repository.otherRepos.RoleRepo;
import com.sobolbetbackend.backendprojektbk1.repository.workerRegistrationRepos.WorkerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminUserEService {
    private final UserRepo userRepo;
    private final WorkerRepo workerRepo;

    @Autowired
    public AdminUserEService(UserRepo userRepo, WorkerRepo workerRepo) {
        this.userRepo = userRepo;
        this.workerRepo = workerRepo;
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

    public UserInfoDTO getUserInfo(Long userId) {
        UserE user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setName(user.getName());
        userInfo.setSurname(user.getSurname());
        userInfo.setEmail(user.getEmail());
        userInfo.setNumberOfPassport(user.getNumberOfPassport());
        userInfo.setPassportIssueDate(user.getPassportIssueDate());
        userInfo.setPassportIssuingAuthority(user.getPassportIssuingAuthority());

        ContractDTO contractDTO = null;

        if (user.getRoles().stream().anyMatch(role ->
                role.getName().equals("ROLE_LINEMAKER") || role.getName().equals("ROLE_SUPPORT"))) {
            Optional<Worker> optionalWorker = workerRepo.findByUserId(userId);
            if(optionalWorker.isPresent()){
                Worker worker = optionalWorker.get();
                contractDTO = new ContractDTO();
                contractDTO.setContractSalary(worker.getContract().getSalary());
                contractDTO.setContractCreationDate(worker.getContract().getCreatedAt());
                contractDTO.setContractDateOfExpiry(worker.getContract().getValid_until());

                userInfo.setRole(worker.getWorkerType());
            }
        }else{
            userInfo.setRole("PLAYER");
        }

        userInfo.setContract(contractDTO);

        return userInfo;
    }



}
