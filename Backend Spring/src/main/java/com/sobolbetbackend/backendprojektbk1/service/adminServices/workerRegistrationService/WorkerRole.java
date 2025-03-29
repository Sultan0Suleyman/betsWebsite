package com.sobolbetbackend.backendprojektbk1.service.adminServices.workerRegistrationService;

import com.sobolbetbackend.backendprojektbk1.entity.common.Worker;
import com.sobolbetbackend.backendprojektbk1.entity.Linemaker;
import com.sobolbetbackend.backendprojektbk1.entity.Support;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Supplier;

public enum WorkerRole {
    LINEMAKER("Linemaker", "ROLE_LINEMAKER", Linemaker::new),
    SUPPORT("Support", "ROLE_SUPPORT", Support::new);

    private final String frontendName;
    @Getter
    private final String dbRoleName;
    private final Supplier<Worker> workerSupplier;

    WorkerRole(String frontendName, String dbRoleName, Supplier<Worker> workerSupplier) {
        this.frontendName = frontendName;
        this.dbRoleName = dbRoleName;
        this.workerSupplier = workerSupplier;
    }

    public Worker createWorkerInstance() {
        return workerSupplier.get();
    }

    public static WorkerRole fromFrontend(String roleName) {
        return Arrays.stream(values())
                .filter(role -> role.frontendName.equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid worker role: " + roleName));
    }
}
