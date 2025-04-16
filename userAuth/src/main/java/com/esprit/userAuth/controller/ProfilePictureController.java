package com.esprit.userAuth.controller;

import com.esprit.userAuth.dtos.UserUpdateDTO;
import com.esprit.userAuth.entities.User;
import com.esprit.userAuth.security.response.MessageResponse;
import com.esprit.userAuth.services.FileStorageService;
import com.esprit.userAuth.services.UserService;
import com.esprit.userAuth.util.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class ProfilePictureController {

    private static final Logger logger = LoggerFactory.getLogger(ProfilePictureController.class);
    private static final String PROFILE_PICTURES_DIR = "profile-pictures";

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthUtil authUtil;

    /**
     * Upload a profile picture for the authenticated user
     * @param file The profile picture file
     * @return A response with the updated user information
     */
    @PostMapping("/picture/upload")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Uploading profile picture, content type: {}, size: {}", 
                    file.getContentType(), file.getSize());
            
            // Check if the file is an image
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Only image files are allowed"));
            }

            // Get the authenticated user
            User user = authUtil.loggedInUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Unauthorized"));
            }

            // Delete the old profile picture if it exists
            if (user.getProfilePicture() != null) {
                fileStorageService.deleteFile(user.getProfilePicture());
            }

            // Store the new profile picture
            String filePath = fileStorageService.storeFile(file, PROFILE_PICTURES_DIR);
            logger.info("Stored profile picture at: {}", filePath);

            // Update the user's profile picture
            UserUpdateDTO updateDTO = new UserUpdateDTO();
            updateDTO.setProfilePicture(filePath);
            User updatedUser = userService.updateUserProfile(user.getUserId(), updateDTO);

            return ResponseEntity.ok(new MessageResponse("Profile picture uploaded successfully"));
        } catch (IOException e) {
            logger.error("Could not upload profile picture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Could not upload profile picture: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error in upload profile picture", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Get the profile picture of a user
     * @return The profile picture file
     */
    @GetMapping("/picture")
    public ResponseEntity<?> getProfilePicture() {
        try {
            // Get the authenticated user if available
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Authenticated user: {}", authentication.getName());
            
            User user = null;
            try {
                user = authUtil.loggedInUser();
            } catch (Exception e) {
                logger.warn("No authenticated user found for profile picture: {}", e.getMessage());
                // Return a default profile picture for anonymous users
                Path defaultPath = Paths.get("uploads", "default-profile.png");
                if (Files.exists(defaultPath)) {
                    Resource defaultResource = new UrlResource(defaultPath.toUri());
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_PNG)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"default-profile.png\"")
                            .body(defaultResource);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }

            // Check if the user has a profile picture
            if (user.getProfilePicture() == null) {
                logger.warn("User has no profile picture");
                return ResponseEntity.notFound().build();
            }

            // Get the file path
            String fileName = user.getProfilePicture().substring(user.getProfilePicture().lastIndexOf("/") + 1);
            Path filePath = fileStorageService.getFilePath(fileName, PROFILE_PICTURES_DIR);
            
            logger.info("Trying to load profile picture: {}", filePath);
            if (!Files.exists(filePath)) {
                logger.warn("Profile picture file not found: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists
            if (!resource.exists()) {
                logger.warn("Resource doesn't exist: {}", resource.getFilename());
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = determineContentType(fileName);
            logger.info("Serving profile picture with content type: {}", contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            logger.error("Malformed URL", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving profile picture", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Delete the profile picture of the authenticated user
     * @return A response indicating success or failure
     */
    @DeleteMapping("/picture")
    public ResponseEntity<?> deleteProfilePicture() {
        try {
            // Get the authenticated user
            User user = authUtil.loggedInUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Unauthorized"));
            }

            // Check if the user has a profile picture
            if (user.getProfilePicture() == null) {
                return ResponseEntity.ok(new MessageResponse("No profile picture to delete"));
            }

            // Delete the profile picture
            boolean deleted = fileStorageService.deleteFile(user.getProfilePicture());

            // Update the user's profile
            UserUpdateDTO updateDTO = new UserUpdateDTO();
            updateDTO.setProfilePicture(null);
            User updatedUser = userService.updateUserProfile(user.getUserId(), updateDTO);

            if (deleted) {
                return ResponseEntity.ok(new MessageResponse("Profile picture deleted successfully"));
            } else {
                return ResponseEntity.ok(new MessageResponse("Profile picture reference removed, but file could not be deleted"));
            }
        } catch (Exception e) {
            logger.error("Error deleting profile picture", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    /**
     * Determine the content type of a file based on its extension
     * @param fileName The name of the file
     * @return The content type
     */
    private String determineContentType(String fileName) {
        if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }
} 