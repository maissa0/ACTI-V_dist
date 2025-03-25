package com.example.gestionabsences.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EtudiantDTO {
    private Long id;
    @NotBlank(message = "Le nom est obligatoire.")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire.")
    private String prenom;

    @Past(message = "La date de naissance doit être dans le passé.")
    private Date dateNaissance;

    @NotNull(message = "La classe ID est obligatoire.")
    private Long classeId;
    // ID de la classe associée
    private List<Long> absences; // Liste des IDs des absences

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }

    public List<Long> getAbsences() {
        return absences;

    }

    public void setAbsences(List<Long> absences) {
        this.absences = absences;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
