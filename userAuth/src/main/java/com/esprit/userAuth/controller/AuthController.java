package com.esprit.userAuth.controller;


import com.esprit.userAuth.dtos.AdminUserCreationDTO;
import com.esprit.userAuth.dtos.UserDTO;
import com.esprit.userAuth.dtos.UserUpdateDTO;
import com.esprit.userAuth.dtos.BasicUserUpdateDTO;
import com.esprit.userAuth.entities.AppRole;
import com.esprit.userAuth.entities.Role;
import com.esprit.userAuth.entities.User;
import com.esprit.userAuth.repositories.RoleRepository;
import com.esprit.userAuth.repositories.UserRepository;
import com.esprit.userAuth.security.jwt.JwtUtils;
import com.esprit.userAuth.security.request.LoginRequest;
import com.esprit.userAuth.security.request.SignupRequest;
import com.esprit.userAuth.security.response.LoginResponse;
import com.esprit.userAuth.security.response.MessageResponse;
import com.esprit.userAuth.security.response.UserInfoResponse;
import com.esprit.userAuth.security.response.BasicUserInfoResponse;
import com.esprit.userAuth.security.services.UserDetailsImpl;
import com.esprit.userAuth.services.TotpService;
import com.esprit.userAuth.services.UserService;
import com.esprit.userAuth.util.AuthUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600 ,allowCredentials = "true")
//@CrossOrigin(origins = "*")

public class AuthController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    TotpService totpService;

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

//      Set the authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Prepare the response body, now including the JWT token directly in the body
        LoginResponse response = new LoginResponse(userDetails.getUsername(),
                roles, jwtToken);

        // Return the response entity with the JWT token included in the response body
        return ResponseEntity.ok(response);
    }


    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Always set default role to ROLE_USER
        Role role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_USER not found."));
        
        // Set user account settings
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("email");
        user.setRole(role);
        
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/public/simple-signup")
    public ResponseEntity<?> simpleSignup(@RequestParam String username,
                                         @RequestParam String email,
                                         @RequestParam String password) {
        // Check if username is already taken
        if (userRepository.existsByUserName(username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Validate input
        if (username.length() < 3 || username.length() > 20) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username must be between 3 and 20 characters!"));
        }

        if (password.length() < 6 || password.length() > 40) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Password must be between 6 and 40 characters!"));
        }

        // Create new user's account
        User user = new User(username, email, encoder.encode(password));

        // Set default role to ROLE_USER
        Role role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_USER not found."));
        
        // Set user account settings
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("email");
        user.setRole(role);
        
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/public/register-user")
    public ResponseEntity<?> registerDefaultUser(@RequestBody Map<String, String> registrationData) {
        String username = registrationData.get("username");
        String email = registrationData.get("email");
        String password = registrationData.get("password");
        String firstName = registrationData.get("firstName");
        String lastName = registrationData.get("lastName");
        
        // Validate required fields
        if (username == null || email == null || password == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username, email, and password are required!"));
        }
        
        // Check if username is already taken
        if (userRepository.existsByUserName(username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account with first name and last name if provided
        User user;
        if (firstName != null && lastName != null) {
            user = new User(username, email, encoder.encode(password), firstName, lastName);
        } else {
            user = new User(username, email, encoder.encode(password));
        }

        // Set default role to ROLE_USER
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_USER not found."));
        
        // Set user account settings
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("email");
        user.setRole(userRole);
        
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully with ROLE_USER!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles,
                user.getFirstName(),
                user.getLastName()


        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }


    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        try{
            userService.generatePasswordResetToken(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent!"));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending password reset email"));
        }

    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @RequestParam String newPassword) {

        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password reset successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    // 2FA Authentication
    @PostMapping("/enable-2fa")
    public ResponseEntity<String> enable2FA() {
        Long userId = authUtil.loggedInUserId();
        GoogleAuthenticatorKey secret = userService.generate2FASecret(userId);
        String qrCodeUrl = totpService.getQrCodeUrl(secret,
                userService.getUserById(userId).getUserName());
        return ResponseEntity.ok(qrCodeUrl);
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<String> disable2FA() {
        Long userId = authUtil.loggedInUserId();
        userService.disable2FA(userId);
        return ResponseEntity.ok("2FA disabled");
    }


    @PostMapping("/verify-2fa")
    public ResponseEntity<String> verify2FA(@RequestParam int code) {
        Long userId = authUtil.loggedInUserId();
        boolean isValid = userService.validate2FACode(userId, code);
        if (isValid) {
            userService.enable2FA(userId);
            return ResponseEntity.ok("2FA Verified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid 2FA Code");
        }
    }


    @GetMapping("/user/2fa-status")
    public ResponseEntity<?> get2FAStatus() {
        User user = authUtil.loggedInUser();
        if (user != null){
            return ResponseEntity.ok().body(Map.of("is2faEnabled", user.isTwoFactorEnabled()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }


    @PostMapping("/public/verify-2fa-login")
    public ResponseEntity<String> verify2FALogin(@RequestParam int code,
                                                 @RequestParam String jwtToken) {
        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        User user = userService.findByUsername(username);
        boolean isValid = userService.validate2FACode(user.getUserId(), code);
        if (isValid) {
            return ResponseEntity.ok("2FA Verified");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid 2FA Code");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String currentPassword,
                                           @RequestParam String newPassword) {
        try {
            // Get the current authenticated user
            Long userId = authUtil.loggedInUserId();
            
            // Verify the current password
            if (!userService.validateCurrentPassword(userId, currentPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Current password is incorrect"));
            }
            
            // Validate the new password
            if (newPassword.length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Password must be at least 8 characters long"));
            }
            
            // Check if new password is the same as the current password
            if (userService.validateCurrentPassword(userId, newPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("New password must be different from the current password"));
            }
            
            // Update the password
            userService.updatePassword(userId, newPassword);
            
            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error changing password: " + e.getMessage()));
        }
    }

    /**
     * Special endpoint to create an initial admin user.
     * This should only be used when setting up the system for the first time.
     * For security reasons, this endpoint will only work if there are no admin users in the system.
     */
    @PostMapping("/public/setup-admin")
    public ResponseEntity<?> setupInitialAdmin(@RequestBody AdminUserCreationDTO adminUserDTO) {
        try {
            // Check if there are any admin users already
            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(user -> user.getRole() != null && 
                             user.getRole().getRoleName() == AppRole.ROLE_ADMIN);
            
            if (adminExists) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Admin user already exists. This endpoint is only for initial setup."));
            }
            
            // Create the admin user
            User adminUser = userService.createAdminUser(
                adminUserDTO.getUsername(),
                adminUserDTO.getEmail(),
                adminUserDTO.getPassword(),
                adminUserDTO.getFirstName(),
                adminUserDTO.getLastName()
            );
            
            return ResponseEntity.ok(new MessageResponse("Initial admin user created successfully with ID: " + adminUser.getUserId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error creating admin user: " + e.getMessage()));
        }
    }

    /**
     * Get the profile of the currently authenticated user
     * @return User profile information
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            // Get the current authenticated user
            User user = authUtil.loggedInUser();
            
            // Convert to DTO to avoid exposing sensitive data
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getUserId());
            userDTO.setUserName(user.getUserName());
            userDTO.setEmail(user.getEmail());
            
            // Include role information if available
            if (user.getRole() != null) {
                userDTO.setRole(user.getRole());
            }
            
            // Include profile picture if available
            if (user.getProfilePicture() != null) {
                userDTO.setProfilePicture(user.getProfilePicture());
            }
            
            // Create a response with additional company information if available
            Map<String, Object> response = new HashMap<>();
            response.put("user", userDTO);
            

            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error retrieving user profile: " + e.getMessage()));
        }
    }

    /**
     * Get detailed profile information for the authenticated user based on their role
     * @return Detailed user profile information
     */
    @GetMapping("/profile/detailed")
    public ResponseEntity<?> getDetailedUserProfile() {
        try {
            // Get the current authenticated user
            User user = authUtil.loggedInUser();
            
            // Base profile information
            Map<String, Object> profileInfo = new HashMap<>();
            profileInfo.put("userId", user.getUserId());
            profileInfo.put("username", user.getUserName());
            profileInfo.put("email", user.getEmail());
            profileInfo.put("firstName", user.getFirstName());
            profileInfo.put("lastName", user.getLastName());
            profileInfo.put("role", user.getRole() != null ? user.getRole().getRoleName().name() : null);
            profileInfo.put("enabled", user.isEnabled());
            profileInfo.put("twoFactorEnabled", user.isTwoFactorEnabled());
            
            // Add profile picture information
            if (user.getProfilePicture() != null) {
                profileInfo.put("profilePicture", user.getProfilePicture());
                // Add a URL to access the profile picture
                profileInfo.put("profilePictureUrl", "/api/profile/picture");
            }
            
            // Add role-specific information
            if (user.getRole() != null) {
                AppRole roleName = user.getRole().getRoleName();
                
                if (roleName == AppRole.ROLE_ADMIN) {
                    // Admin-specific information
                    profileInfo.put("roleType", "Administrator");
                    profileInfo.put("permissions", "Full system access");
                    
                    // Get count of users in the system
                    long userCount = userRepository.count();
                    profileInfo.put("managedUsers", userCount);
                    
                } else if (roleName == AppRole.ROLE_COMPANY) {
                    // Company-specific information
                    profileInfo.put("roleType", "Company Manager");
                    

                    
                } else if (roleName == AppRole.ROLE_EMPLOYEE) {
                    // Employee-specific information
                    profileInfo.put("roleType", "Employee");
                    

                    
                } else if (roleName == AppRole.ROLE_USER) {
                    // Regular user information
                    profileInfo.put("roleType", "Standard User");
                }
            }
            
            // Add account status information
            Map<String, Object> accountStatus = new HashMap<>();
            accountStatus.put("accountNonLocked", user.isAccountNonLocked());
            accountStatus.put("accountNonExpired", user.isAccountNonExpired());
            accountStatus.put("credentialsNonExpired", user.isCredentialsNonExpired());
            accountStatus.put("accountExpiryDate", user.getAccountExpiryDate());
            accountStatus.put("credentialsExpiryDate", user.getCredentialsExpiryDate());
            
            profileInfo.put("accountStatus", accountStatus);
            
            return ResponseEntity.ok(profileInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error retrieving detailed user profile: " + e.getMessage()));
        }
    }

    /**
     * Simple test endpoint to verify authentication is working
     * @return A simple message with the authenticated username
     */
    @GetMapping("/profile/test")
    public ResponseEntity<?> testAuthenticatedEndpoint() {
        try {
            User user = authUtil.loggedInUser();
            return ResponseEntity.ok(new MessageResponse("Hello, " + user.getUserName() + "! You are authenticated."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Update the authenticated user's profile information
     * @param updateDTO DTO containing the fields to update
     * @return Updated user profile information
     */
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            // Get the current authenticated user
            User user = authUtil.loggedInUser();
            
            // Update the user profile
            User updatedUser = userService.updateUserProfile(user.getUserId(), updateDTO);
            
            // Convert to DTO to avoid exposing sensitive data
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(updatedUser.getUserId());
            userDTO.setUserName(updatedUser.getUserName());
            userDTO.setEmail(updatedUser.getEmail());
            
            // Include role information if available
            if (updatedUser.getRole() != null) {
                userDTO.setRole(updatedUser.getRole());
            }
            
            // Set other fields
            userDTO.setAccountNonLocked(updatedUser.isAccountNonLocked());
            userDTO.setAccountNonExpired(updatedUser.isAccountNonExpired());
            userDTO.setCredentialsNonExpired(updatedUser.isCredentialsNonExpired());
            userDTO.setEnabled(updatedUser.isEnabled());
            userDTO.setTwoFactorEnabled(updatedUser.isTwoFactorEnabled());
            userDTO.setCreatedDate(updatedUser.getCreatedDate());
            userDTO.setUpdatedDate(updatedUser.getUpdatedDate());
            
            // Create a response with additional company information if available
            Map<String, Object> response = new HashMap<>();
            response.put("user", userDTO);
            

            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error updating profile: " + e.getMessage()));
        }
    }

    /**
     * Update only the basic information of the authenticated user (username, email, firstName, lastName)
     * @param updateDTO DTO containing the basic fields to update
     * @return Updated user basic information
     */
    @PutMapping("/profile/update-basic-info")
    public ResponseEntity<?> updateBasicUserInfo(@Valid @RequestBody BasicUserUpdateDTO updateDTO) {
        try {
            // Get the current authenticated user
            User user = authUtil.loggedInUser();
            
            // Update the user basic info
            User updatedUser = userService.updateBasicUserInfo(user.getUserId(), updateDTO);
            
            // Create the response with updated info
            BasicUserInfoResponse response = new BasicUserInfoResponse(
                updatedUser.getUserName(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getRole() != null ? updatedUser.getRole().getRoleName().name() : "N/A"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error updating basic info: " + e.getMessage()));
        }
    }

    @GetMapping("/user/basic-info")
    public ResponseEntity<?> getBasicUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        
        // Extract role name from the user's role
        String roleName = user.getRole() != null ? user.getRole().getRoleName().name() : "N/A";
        
        BasicUserInfoResponse response = new BasicUserInfoResponse(
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roleName
        );
        
        return ResponseEntity.ok(response);
    }

}
