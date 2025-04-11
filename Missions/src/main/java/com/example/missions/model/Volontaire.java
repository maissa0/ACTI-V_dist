package com.example.missions.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Volontaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private String telephone;

    private String adresse;

    @ManyToMany(mappedBy = "membres")
    private List<Equipe> equipes;

    @OneToMany(mappedBy = "responsable")
    private List<Equipe> equipesDirigees;
}

