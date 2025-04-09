package com.example.evaluationevenement.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "evaluationEvenement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationEvenement {
    @Id
    private String id;
    private String evenementId;
    private int note;
    private String commentaire;
}