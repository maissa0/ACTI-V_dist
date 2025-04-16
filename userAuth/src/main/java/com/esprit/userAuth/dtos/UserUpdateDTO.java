package com.esprit.userAuth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    @Size(max = 50)
    @Email
    private String email;
    
    @Size(max = 120)
    private String firstName;
    
    @Size(max = 120)
    private String lastName;
    
    private Boolean twoFactorEnabled;
    
    private String profilePicture;
} 