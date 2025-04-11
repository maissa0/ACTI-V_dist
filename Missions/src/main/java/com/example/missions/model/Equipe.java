package com.example.missions.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomEquipe;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToMany
    @JoinTable(
            name = "equipe_volontaires",
            joinColumns = @JoinColumn(name = "equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "volontaire_id")
    )
    private List<Volontaire> membres;

    @ManyToOne
    @JoinColumn(name = "responsable_id")
    private Volontaire responsable;
}

