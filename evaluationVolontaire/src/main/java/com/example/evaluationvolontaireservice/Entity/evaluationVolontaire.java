package com.example.evaluationvolontaireservice.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "evaluationVolontaire") // ✅ Ajout du " fermant
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class evaluationVolontaire {
    @Id
    private String id;
    private String volontaireId;
    private int note;
    private String commentaire;
}

