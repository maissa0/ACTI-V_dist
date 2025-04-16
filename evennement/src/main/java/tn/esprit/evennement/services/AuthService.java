package tn.esprit.evennement.services;

import org.springframework.stereotype.Service;
import tn.esprit.evennement.clients.UserAuthClient;


@Service
public class AuthService {
    private final UserAuthClient userAuthClient;

    public AuthService(UserAuthClient userAuthClient) {
        this.userAuthClient = userAuthClient;
    }

    public Long validateToken(String token) {
        return userAuthClient.getUserIdByUsername(token);
    }
} 