package tn.esprit.competences.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.competences.services.GeminiAiService;

import java.util.Map;

@RestController
@RequestMapping("/gemini")
public class GeminiAiController {

    private final GeminiAiService geminiAiService;

    @Autowired
    public GeminiAiController(GeminiAiService geminiAiService) {
        this.geminiAiService = geminiAiService;
    }

    /**
     * Generate competence recommendations based on user background
     * @param requestBody Map containing the user background information
     * @return Recommended competencies
     */
    @PostMapping("/recommendations")
    public ResponseEntity<String> getCompetenceRecommendations(@RequestBody Map<String, String> requestBody) {
        String userBackground = requestBody.get("userBackground");
        if (userBackground == null || userBackground.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User background information is required");
        }
        
        String recommendations = geminiAiService.generateCompetenceRecommendations(userBackground);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Analyze a competence description and provide improvement suggestions
     * @param requestBody Map containing the competence description
     * @return Analysis and suggestions
     */
    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeCompetenceDescription(@RequestBody Map<String, String> requestBody) {
        String competenceDescription = requestBody.get("competenceDescription");
        if (competenceDescription == null || competenceDescription.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Competence description is required");
        }
        
        String analysis = geminiAiService.analyzeCompetenceDescription(competenceDescription);
        return ResponseEntity.ok(analysis);
    }
} 