package com.esprit.userAuth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicUserUpdateDTO {
    
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String userName;
    
    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email cannot exceed 50 characters")
    private String email;
    
    @Size(max = 120, message = "First name cannot exceed 120 characters")
    private String firstName;
    
    @Size(max = 120, message = "Last name cannot exceed 120 characters")
    private String lastName;
} 