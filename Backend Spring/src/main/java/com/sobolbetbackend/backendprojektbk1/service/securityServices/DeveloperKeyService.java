package com.sobolbetbackend.backendprojektbk1.service.securityServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DeveloperKeyService {

    private final String developerKey;

    public DeveloperKeyService(@Value("${app.developer-key}") String developerKey) {
        this.developerKey = developerKey;
    }

    public String getDeveloperKey() {
        return developerKey;
    }
}
