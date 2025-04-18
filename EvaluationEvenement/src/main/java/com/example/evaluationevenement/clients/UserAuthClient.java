package com.example.evaluationevenement.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userAuth", url = "http://localhost:8081/api/userAuth")
public interface UserAuthClient {

    /**
     * Gets a user's ID from their JWT token
     * This endpoint expects the full "Bearer token" format
     */
    @GetMapping("/api/users/username")
    Long getUserIdByUsername(@RequestHeader("Authorization") String token);

    /**
     * Gets a user's ID from their JWT token using a public endpoint
     * This endpoint doesn't require authentication
     */
    @GetMapping("/api/public/user-id")
    Long getUserIdByUsername2(@RequestHeader("Authorization") String token);

    /**
     * Gets a user's ID by username directly
     * This endpoint is public and doesn't require authentication
     */
    @GetMapping("/api/public/user-id/{username}")
    Long getUserIdByUsername3(@PathVariable("username") String username);
} 