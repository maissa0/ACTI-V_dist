package tn.esprit.competences.dto;

import lombok.Data;

@Data
public class CompetenceRequest {
    private String name;
    private String description;
    private String level;
    private Long userId;
} 