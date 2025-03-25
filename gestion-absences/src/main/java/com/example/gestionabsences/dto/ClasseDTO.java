package com.example.gestionabsences.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ClasseDTO {
    private Long id;
    @NotBlank(message = "Le nom de la classe est obligatoire.")
    private String nom;

    @NotNull(message = "Le niveau est obligatoire.")
    @Min(value = 1, message = "Le niveau doit être supérieur ou égal à 1.")
    @Max(value = 6, message = "Le niveau doit être inférieur ou égal à 6.")
    private Integer niveau;
    private List<Long> etudiants;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public List<Long> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<Long> etudiants) {
        this.etudiants = etudiants;


    }
}
