package com.sobolbetbackend.backendprojektbk1.service.otherServices;

import com.sobolbetbackend.backendprojektbk1.entity.other.RevokedRefreshToken;
import com.sobolbetbackend.backendprojektbk1.repository.otherRepos.RevokedRefreshTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenRevocationService {
    private final RevokedRefreshTokenRepo revokedRefreshTokenRepo;

    @Autowired
    public RefreshTokenRevocationService(RevokedRefreshTokenRepo revokedRefreshTokenRepo) {
        this.revokedRefreshTokenRepo = revokedRefreshTokenRepo;
    }
    public void revokeToken(String token) {
        revokedRefreshTokenRepo.save(new RevokedRefreshToken(token));
    }
    public boolean isTokenRevoked(String token) {
        return revokedRefreshTokenRepo.existsByToken(token);
    }
    @Transactional
    public void deleteAllRecords() {
        revokedRefreshTokenRepo.deleteAll();
    }
}
