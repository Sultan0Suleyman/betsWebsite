package com.sobolbetbackend.backendprojektbk1.util.security.others;

import com.sobolbetbackend.backendprojektbk1.service.otherServices.RefreshTokenRevocationService;
import com.sobolbetbackend.backendprojektbk1.util.security.constants.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeyUpdater {
    private final RefreshTokenRevocationService refreshTokenRevocationService;
    @Autowired
    public KeyUpdater(RefreshTokenRevocationService refreshTokenRevocationService) {
        this.refreshTokenRevocationService = refreshTokenRevocationService;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // каждые 24 часа
    public void updateKey() {
        SecurityConstants.generateNewSecretKey();
        refreshTokenRevocationService.deleteAllRecords();
    }
}
