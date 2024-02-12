package com.sobolbetbackend.backendprojektbk1.service.securityServices;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

@Service
public class DeveloperKeyService {

    private String developerKey;

    @PostConstruct
    public void initializeKey() {
        this.developerKey = generateDeveloperKey();
        writeKeyToFile(this.developerKey, "C:\\Users\\mrlen\\Desktop\\developer-key.txt");
    }
    public String getDeveloperKey() {
        return this.developerKey;
    }

    private String generateDeveloperKey() {
        // Генерация случайного ключа (ваша логика может быть другой)
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[16];
        random.nextBytes(keyBytes);
        return bytesToHex(keyBytes);
    }

    private void writeKeyToFile(String key, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(key);
            System.out.println("Developer Key has been written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }
}
