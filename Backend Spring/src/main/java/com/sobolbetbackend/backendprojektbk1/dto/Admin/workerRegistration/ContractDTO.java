package com.sobolbetbackend.backendprojektbk1.dto.Admin.workerRegistration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractDTO {
    private LocalDate contractCreationDate;
    private LocalDate contractDateOfExpiry;
    private double contractSalary;
}
