package tn.esprit.competences.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Competence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private String level; // Beginner, Intermediate, Advanced, Expert
    
    @Column(name = "user_id")
    private Long userId; // Reference to the user in the auth service
} 