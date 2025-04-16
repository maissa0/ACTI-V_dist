package com.esprit.userAuth.controller;

import com.esprit.userAuth.entities.User;
import com.esprit.userAuth.security.jwt.JwtUtils;
import com.esprit.userAuth.security.response.BasicUserInfoResponse;
import com.esprit.userAuth.security.services.UserDetailsServiceImpl;
import com.esprit.userAuth.services.UserService;
import com.esprit.userAuth.services.PdfGenerationService;
import com.esprit.userAuth.utils.GeminiAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "${frontend.url}", maxAge = 3600, allowCredentials = "true")
public class UserController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    @Autowired
    private GeminiAiService geminiAiService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/username")
    public ResponseEntity<Long> getUserIdByUsername(@RequestHeader("Authorization") String token) { 
        try {
            // Extract the token from the Authorization header
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get username from the token and use it to find the user ID
            String username = jwtUtils.getUserNameFromJwtToken(token);
            Long userId = userDetailsService.getUserIdByUsername(username);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    /**
     * Get all users with ROLE_USER
     * @return List of basic user information
     */
    @GetMapping("/role/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BasicUserInfoResponse>> getAllUsersWithUserRole() {
        List<User> users = userService.getAllUsersWithRoleUser();
        
        List<BasicUserInfoResponse> userResponses = users.stream()
            .map(user -> new BasicUserInfoResponse(
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "ROLE_USER"
            ))
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(userResponses);
    }

    /**
     * Generate a PDF report for a user
     * @param userId ID of the user to generate a report for
     * @return PDF document as a byte array
     */
    @GetMapping("/report/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateUserReport(@PathVariable("userId") Long userId) {
        try {
            byte[] pdfContent = pdfGenerationService.generateUserReport(userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "user-report.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Generate a PDF report for a user with an AI-generated summary
     * @param userId ID of the user to generate a report for
     * @param requestBody Request body containing the AI summary
     * @return PDF document as a byte array
     */
    @PostMapping("/report/{userId}/with-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateUserReportWithAiSummary(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, String> requestBody) {
        try {
            String aiSummary = requestBody.get("aiSummary");
            
            byte[] pdfContent = pdfGenerationService.generateUserReportWithAiSummary(userId, aiSummary);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "user-report.pdf");
            
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return Basic user information
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BasicUserInfoResponse> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            BasicUserInfoResponse response = new BasicUserInfoResponse(
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().getRoleName().toString()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}