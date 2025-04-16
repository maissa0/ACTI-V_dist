package com.esprit.userAuth.services.impl;
import com.esprit.userAuth.dtos.UserDTO;
import com.esprit.userAuth.dtos.UserUpdateDTO;
import com.esprit.userAuth.dtos.BasicUserUpdateDTO;
import com.esprit.userAuth.entities.AppRole;
import com.esprit.userAuth.entities.PasswordResetToken;
import com.esprit.userAuth.entities.Role;
import com.esprit.userAuth.entities.User;
import com.esprit.userAuth.repositories.PasswordResetTokenRepository;
import com.esprit.userAuth.repositories.RoleRepository;
import com.esprit.userAuth.repositories.UserRepository;
import com.esprit.userAuth.services.TotpService;
import com.esprit.userAuth.services.UserService;
import com.esprit.userAuth.util.EmailService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.esprit.userAuth.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {

    @Value("${frontend.url}")
    String frontendUrl;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TotpService totpService;



    @Override
    public void updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        AppRole appRole = AppRole.valueOf(roleName);
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserDTO getUserById(Long id) {
//        return userRepository.findById(id).orElseThrow();
        User user = userRepository.findById(id).orElseThrow();
        return convertToDto(user);
    }

    private UserDTO convertToDto(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.getTwoFactorSecret(),
                user.isTwoFactorEnabled(),
                user.getSignUpMethod(),
                user.getRole(),
                user.getCreatedDate(),
                user.getUpdatedDate(),
                user.getProfilePicture()
        );
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }


    @Override
    public void updateAccountLockStatus(Long userId, boolean lock) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!lock);
        userRepository.save(user);
    }


    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void updateAccountExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setAccountNonExpired(!expire);
        userRepository.save(user);
    }

    @Override
    public void updateAccountEnabledStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void updateCredentialsExpiryStatus(Long userId, boolean expire) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new RuntimeException("User not found"));
        user.setCredentialsNonExpired(!expire);
        userRepository.save(user);
    }


    @Override
    public void updatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password");
        }
    }

    @Override
    public void generatePasswordResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(24, ChronoUnit.HOURS);
        PasswordResetToken resetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRepository.save(resetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        // Send email to user
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (resetToken.isUsed())
            throw new RuntimeException("Password reset token has already been used");

        if (resetToken.getExpiryDate().isBefore(Instant.now()))
            throw new RuntimeException("Password reset token has expired");

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(User user){
        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public GoogleAuthenticatorKey generate2FASecret(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        GoogleAuthenticatorKey key = totpService.generateSecret();
        user.setTwoFactorSecret(key.getKey());
        userRepository.save(user);
        return key;
    }

    @Override
    public boolean validate2FACode(Long userId, int code){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return totpService.verifyCode(user.getTwoFactorSecret(), code);
    }

    @Override
    public void enable2FA(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void disable2FA(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setTwoFactorEnabled(false);
        userRepository.save(user);
    }

    @Override
    public boolean validateCurrentPassword(Long userId, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    @Override
    public User createAdminUser(String username, String email, String password, String firstName, String lastName) {
        // Check if username already exists
        if (userRepository.existsByUserName(username)) {
            throw new RuntimeException("Username is already taken");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use");
        }
        
        // Get the admin role
        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        
        // Create the admin user
        User adminUser = new User();
        adminUser.setUserName(username);
        adminUser.setEmail(email);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setFirstName(firstName);
        adminUser.setLastName(lastName);
        adminUser.setRole(adminRole);
        
        // Set account settings
        adminUser.setAccountNonLocked(true);
        adminUser.setAccountNonExpired(true);
        adminUser.setCredentialsNonExpired(true);
        adminUser.setEnabled(true);
        adminUser.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        adminUser.setAccountExpiryDate(LocalDate.now().plusYears(1));
        adminUser.setTwoFactorEnabled(false);
        adminUser.setSignUpMethod("admin_creation");
        
        // Save and return the admin user
        return userRepository.save(adminUser);
    }

    @Override
    public User createEmployeeUser(String username, String email, String firstName, String lastName, 
                                  Long companyId) {
        // Check if username already exists
        if (userRepository.existsByUserName(username)) {
            throw new RuntimeException("Username is already taken");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use");
        }
        
        // Get the company
        //Company company = companyRepository.findById(companyId)
            //    .orElseThrow(() -> new RuntimeException("Company not found"));
        
        // Get the employee role
        Role employeeRole = roleRepository.findByRoleName(AppRole.ROLE_EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Employee role not found"));
        
        // Generate a secure password
        String generatedPassword = PasswordGenerator.generateSecurePassword();
        
        // Create the employee user
        User employeeUser = new User();
        employeeUser.setUserName(username);
        employeeUser.setEmail(email);
        employeeUser.setPassword(passwordEncoder.encode(generatedPassword));
        employeeUser.setFirstName(firstName);
        employeeUser.setLastName(lastName);
        employeeUser.setRole(employeeRole);
        //employeeUser.setCompany(company);
        
        // Set account settings
        employeeUser.setAccountNonLocked(true);
        employeeUser.setAccountNonExpired(true);
        employeeUser.setCredentialsNonExpired(true);
        employeeUser.setEnabled(true);
        employeeUser.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        employeeUser.setAccountExpiryDate(LocalDate.now().plusYears(1));
        employeeUser.setTwoFactorEnabled(false);
        employeeUser.setSignUpMethod("company_creation");
        
        // Save the employee user
        User savedUser = userRepository.save(employeeUser);
        
        // Send email with credentials
       // emailService.sendEmployeeRegistrationEmail(
       //         email,
       //         firstName + " " + lastName,
       //         company.getName(),
       //         username,
        //        generatedPassword
        //);
        
        return savedUser;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsersWithRoleUser() {
        return userRepository.findByRole_RoleName(AppRole.ROLE_USER);
    }
    
    @Override
    public User updateUserProfile(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Check if email is being updated and if it's already in use by another user
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email is already in use by another account");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        // Update first name if provided
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        
        // Update last name if provided
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        
        // Update profile picture if provided
        if (updateDTO.getProfilePicture() != null) {
            user.setProfilePicture(updateDTO.getProfilePicture());
        }
        
        // Update two-factor authentication status if provided
        if (updateDTO.getTwoFactorEnabled() != null) {
            // Only update if the value is different from current setting
            if (updateDTO.getTwoFactorEnabled() != user.isTwoFactorEnabled()) {
                if (updateDTO.getTwoFactorEnabled()) {
                    // If enabling 2FA, make sure the secret is generated
                    if (user.getTwoFactorSecret() == null) {
                        GoogleAuthenticatorKey key = totpService.generateSecret();
                        user.setTwoFactorSecret(key.getKey());
                    }
                }
                user.setTwoFactorEnabled(updateDTO.getTwoFactorEnabled());
            }
        }
        
        return userRepository.save(user);
    }

    @Override
    public User updateBasicUserInfo(Long userId, BasicUserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Check if username is being updated and if it's already in use by another user
        if (updateDTO.getUserName() != null && !updateDTO.getUserName().equals(user.getUserName())) {
            if (userRepository.existsByUserName(updateDTO.getUserName())) {
                throw new RuntimeException("Username is already in use by another account");
            }
            user.setUserName(updateDTO.getUserName());
        }
        
        // Check if email is being updated and if it's already in use by another user
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email is already in use by another account");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        // Update first name if provided
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        
        // Update last name if provided
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        
        return userRepository.save(user);
    }
}
