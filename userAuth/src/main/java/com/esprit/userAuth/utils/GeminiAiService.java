package com.esprit.userAuth.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiAiService {

    @Value("${google.gemini.api-key}")
    private String geminiApiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiAiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generate a summary about a user based on their competences
     * @param username The username
     * @param competences List of user competences
     * @return AI-generated summary
     */
    public String generateUserSummary(String username, List<String> competences) {
        try {
            // Create the prompt
            String prompt = String.format(
                "Based on the following competences for the user %s, provide a professional 2-paragraph summary " +
                "of their likely professional profile and strengths. Here are their competences: %s",
                username,
                String.join(", ", competences)
            );

            // Prepare the API request
            Map<String, Object> request = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            
            parts.put("text", prompt);
            content.put("parts", List.of(parts));
            request.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Add API key as a query parameter
            String url = geminiApiUrl + "?key=" + geminiApiKey;
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            // Call the Gemini API
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parse the JSON response
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                
                // Extract the generated text from the response
                return jsonResponse
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("Failed to generate summary");
            }
            
            return "Unable to generate summary at this time.";
        } catch (Exception e) {
            return "Error generating summary: " + e.getMessage();
        }
    }
} 