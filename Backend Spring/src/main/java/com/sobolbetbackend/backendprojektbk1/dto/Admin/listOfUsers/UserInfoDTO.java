package com.sobolbetbackend.backendprojektbk1.dto.Admin.listOfUsers;

import com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration.ContractDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private String name;
    private String surname;
    private String email;
    private String numberOfPassport;
    private LocalDate passportIssueDate;
    private String passportIssuingAuthority;
    private String role;

    private ContractDTO contract;
}
