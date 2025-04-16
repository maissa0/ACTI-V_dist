package com.example.evaluationevenement.service;


import com.example.evaluationevenement.clients.UserAuthClient;
import org.springframework.stereotype.Service;

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