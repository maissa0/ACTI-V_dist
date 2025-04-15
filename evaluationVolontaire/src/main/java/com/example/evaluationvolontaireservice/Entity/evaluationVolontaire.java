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

    private int note;
    private String commentaire;
    private String userId;

    public static String getUserId() {
        return "static-user-id";
    }

    public void setUserId(String userId) {
        this.userId = userId != null ? userId : getUserId();
    }
}

