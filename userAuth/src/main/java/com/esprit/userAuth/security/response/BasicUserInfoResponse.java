package com.esprit.userAuth.security.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BasicUserInfoResponse {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public BasicUserInfoResponse(String username, String email, String firstName, String lastName, String role) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
} 