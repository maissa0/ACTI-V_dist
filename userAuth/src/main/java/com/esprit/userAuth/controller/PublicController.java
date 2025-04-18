package com.esprit.userAuth.controller;

import com.esprit.userAuth.security.jwt.JwtUtils;
import com.esprit.userAuth.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "${frontend.url}", maxAge = 3600, allowCredentials = "true")
public class PublicController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @GetMapping("/user-id")
    public ResponseEntity<Long> getUserIdByUsername2(@RequestHeader("Authorization") String token) {
        try {
            // Extract the token from the Authorization header
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Try to extract the username
            try {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                if (username == null || username.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                
                Long userId = userDetailsService.getUserIdByUsername(username);
                return ResponseEntity.ok(userId);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    @GetMapping("/user-id/{username}")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String username) {
        try {
            Long userId = userDetailsService.getUserIdByUsername(username);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        }
    }
    
    /**
     * Simple endpoint to get user ID by username
     * No authentication needed
     */
    @GetMapping("/simple/user/{username}")
    public Long getSimpleUserIdByUsername(@PathVariable String username) {
        try {
            return userDetailsService.getUserIdByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get username by user ID
     * No authentication needed
     */
    @GetMapping("/username/{userId}")
    public String getUsernameById(@PathVariable Long userId) {
        try {
            return userDetailsService.getUsernameById(userId);
        } catch (Exception e) {
            return "Unknown User";
        }
    }
} 