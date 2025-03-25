package com.example.gestionabsences.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AbsenceDTO {
    private Long id;

    @NotNull(message = "La date de l'absence est obligatoire.")
    @PastOrPresent(message = "La date de l'absence ne peut pas être dans le futur.")
    private LocalDate date;

    @NotBlank(message = "La raison de l'absence est obligatoire.")
    @Size(max = 255, message = "La raison ne peut pas dépasser 255 caractères.")
    private String raison;

    private boolean justifiee;

    @NotNull(message = "L'identifiant de l'étudiant est obligatoire.")
    private Long etudiantId;


    public Long getEtudiantId(){
        return etudiantId;
    }

    public void setEtudiantId(Long etudiantId){
        this.etudiantId = etudiantId;
    }


    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public LocalDate getDate(){
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public String getRaison(){
        return raison;
    }

    public void setRaison(String raison){
        this.raison = raison;
    }

    public boolean isJustifiee(){
        return justifiee;
    }

    public void setJustifiee(boolean justifiee){
        this.justifiee = justifiee;
    }


}
