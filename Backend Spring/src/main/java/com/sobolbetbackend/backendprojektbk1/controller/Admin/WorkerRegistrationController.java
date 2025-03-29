package com.sobolbetbackend.backendprojektbk1.controller.Admin;

import com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration.WorkerDTO;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.exception.EmailAlreadyExistsException;
import com.sobolbetbackend.backendprojektbk1.exception.UserAlreadyRegisteredException;
import com.sobolbetbackend.backendprojektbk1.service.adminServices.workerRegistrationService.WorkerRegistrationService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class WorkerRegistrationController {
    private final WorkerRegistrationService workerRegistrationService;

    @Autowired
    public WorkerRegistrationController(WorkerRegistrationService workerRegistrationService) {
        this.workerRegistrationService = workerRegistrationService;
    }

    @PostMapping("/worker-registration")
    public ResponseEntity<String> registration(@RequestBody WorkerDTO worker) {
        try {
            workerRegistrationService.registration(worker);
            return ResponseEntity.ok().body("{\"message\": \"Worker was successfully registered\"}");
        } catch (UserAlreadyRegisteredException | EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Exception happened\"}");

        }
    }
}
