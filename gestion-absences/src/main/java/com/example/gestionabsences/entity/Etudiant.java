package com.example.gestionabsences.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name = "etudiants")
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Temporal(TemporalType.DATE)
    private Date dateNaissance;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    private List<Absence> absences;

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

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public List<Absence> getAbsences() {
        return absences;

    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }


}
