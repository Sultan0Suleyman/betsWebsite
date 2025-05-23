package com.sobolbetbackend.backendprojektbk1.service.adminServices.workerRegistrationService;

import com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration.ContractDTO;
import com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration.WorkerCredentialDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Linemaker;
import com.sobolbetbackend.backendprojektbk1.entity.Support;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.common.Worker;
import com.sobolbetbackend.backendprojektbk1.entity.other.Contract;
import com.sobolbetbackend.backendprojektbk1.entity.other.Role;
import com.sobolbetbackend.backendprojektbk1.exception.EmailAlreadyExistsException;
import com.sobolbetbackend.backendprojektbk1.exception.UserAlreadyRegisteredException;
import com.sobolbetbackend.backendprojektbk1.repository.otherRepos.RoleRepo;
import com.sobolbetbackend.backendprojektbk1.repository.workerRegistrationRepos.LinemakerRepo;
import com.sobolbetbackend.backendprojektbk1.repository.workerRegistrationRepos.SupportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


@Service
public class WorkerRegistrationService {
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final LinemakerRepo linemakerRepo;
    private final SupportRepo supportRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WorkerRegistrationService(RoleRepo roleRepo, UserRepo userRepo, LinemakerRepo linemakerRepo,
                                     SupportRepo supportRepo, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.linemakerRepo = linemakerRepo;
        this.supportRepo = supportRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registration(WorkerCredentialDTO workerCredentialDTO) throws UserAlreadyRegisteredException, EmailAlreadyExistsException {
        if(userRepo.findByEmail(workerCredentialDTO.getEmail())!=null){
            throw new EmailAlreadyExistsException("User with this email already exists!");
        }if(userRepo.findByNumberOfPassport(workerCredentialDTO.getNumberOfPassport())!=null){
            throw new UserAlreadyRegisteredException("User already has an account on our service");
        }

        WorkerRole workerRole = WorkerRole.fromFrontend(workerCredentialDTO.getRole());

        Role dbRole = roleRepo.findByName(workerRole.getDbRoleName());

        UserE user = userRepo.save(saveWorkerDataIntoDatabase(workerCredentialDTO,dbRole));

        Worker worker = workerRole.createWorkerInstance();

        worker.setUser(user);
        Contract contract = getContractInstanceFromFrontend(workerCredentialDTO.getContract(),worker);

        worker.setContract(contract);

        saveWorker(worker);

    }

    private void saveWorker(Worker worker) {
        if (worker instanceof Linemaker) {
            linemakerRepo.save((Linemaker) worker);
        } else if (worker instanceof Support) {
            supportRepo.save((Support) worker);
        } else {
            throw new RuntimeException("Unknown worker type: " + worker.getClass().getSimpleName());
        }
    }


    private Contract getContractInstanceFromFrontend(ContractDTO contract, Worker worker){
        Contract contract1 = new Contract();
        contract1.setCreatedAt(contract.getContractCreationDate());
        contract1.setSalary(contract.getContractSalary());
        contract1.setValid_until(contract.getContractDateOfExpiry());
        contract1.setWorker(worker);

        return contract1;
    }

    private UserE saveWorkerDataIntoDatabase(WorkerCredentialDTO workerCredentialDTO, Role workerRole){
        UserE user = new UserE();
        user.setPassword(passwordEncoder.encode(workerCredentialDTO.getPassword()));
        user.setRoles(Collections.singletonList(workerRole));
        user.setName(workerCredentialDTO.getName());
        user.setEmail(workerCredentialDTO.getEmail());
        user.setSurname(workerCredentialDTO.getSurname());
        user.setNumberOfPassport(workerCredentialDTO.getNumberOfPassport());
        user.setPassportIssueDate(workerCredentialDTO.getPassportIssueDate());
        user.setPassportIssuingAuthority(workerCredentialDTO.getPassportIssuingAuthority());

        return user;
    }
}
