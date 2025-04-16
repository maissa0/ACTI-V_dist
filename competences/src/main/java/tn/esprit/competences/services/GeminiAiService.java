package tn.esprit.competences.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiAiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAiService.class);

    @Value("${google.gemini.api-key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiAiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String generateContent(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> request = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of(
                                    "text", prompt
                            ))
                    )),
                    "safetySettings", List.of(Map.of(
                            "category", "HARM_CATEGORY_DANGEROUS_CONTENT",
                            "threshold", "BLOCK_ONLY_HIGH"
                    )),
                    "generationConfig", Map.of(
                            "temperature", 0.9,
                            "topP", 1,
                            "maxOutputTokens", 2048
                    )
            );

            String url = String.format("%s?key=%s", apiUrl, apiKey);
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            return parseResponse(response);
        } catch (Exception e) {
            logger.error("Gemini API error", e);
            return "Error: " + e.getMessage();
        }
    }

    private String parseResponse(Map<String, Object> response) {
        try {
            return ((List<Map<String, Object>>) response.get("candidates"))
                    .stream()
                    .findFirst()
                    .map(c -> (Map<String, Object>) c.get("content"))
                    .map(c -> (List<Map<String, Object>>) c.get("parts"))
                    .flatMap(p -> p.stream().findFirst())
                    .map(p -> (String) p.get("text"))
                    .orElse("No response content found");
        } catch (Exception e) {
            logger.error("Error parsing response", e);
            return "Error parsing response: " + e.getMessage();
        }
    }

    public String generateCompetenceRecommendations(String userBackground) {
        return "Competence recommendations";
    }

    public String analyzeCompetenceDescription(String competenceDescription) {
        return "Competence description";
    }
}