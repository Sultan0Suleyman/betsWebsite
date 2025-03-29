package com.sobolbetbackend.backendprojektbk1.util.security.constants;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SecurityConstants {
    private static SecretKey currentSecretKey;
    private static final Lock lock = new ReentrantLock();
    public static final long JWT_ACCESS_TOKEN_EXPIRATION = 20*60*1000;
    public static final long JWT_REFRESH_TOKEN_EXPIRATION = 24*60*60*1000;


    public static void generateNewSecretKey() {
        lock.lock();
        try {
            SecureRandom secureRandom = new SecureRandom();
            int keyLengthInBytes = 32;
            byte[] keyBytes = new byte[keyLengthInBytes];
            secureRandom.nextBytes(keyBytes);
            currentSecretKey = Keys.hmacShaKeyFor(keyBytes);
        } finally {
            lock.unlock();
        }
    }

    public static SecretKey getCurrentSecretKey() {
        return currentSecretKey;
    }
}
