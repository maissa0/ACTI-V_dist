package com.esprit.userAuth.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service for handling file storage operations
 */
public interface FileStorageService {
    
    /**
     * Store a file in the file system
     * @param file The file to store
     * @param directory The directory to store the file in
     * @return The path to the stored file
     * @throws IOException If an I/O error occurs
     */
    String storeFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * Delete a file from the file system
     * @param filePath The path to the file to delete
     * @return true if the file was deleted, false otherwise
     */
    boolean deleteFile(String filePath);
    
    /**
     * Get the path to a file
     * @param fileName The name of the file
     * @param directory The directory where the file is stored
     * @return The path to the file
     */
    Path getFilePath(String fileName, String directory);
} 