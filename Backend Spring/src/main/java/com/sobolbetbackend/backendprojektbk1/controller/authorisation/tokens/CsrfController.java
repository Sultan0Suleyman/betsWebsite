package com.sobolbetbackend.backendprojektbk1.controller.authorisation.tokens;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CsrfController {

    @GetMapping("/csrf/token")
    public Map<String, String> csrf(CsrfToken token){
        Map<String, String> response = new HashMap<>();
        response.put("token", token.getToken());
        return response;
    }
}
