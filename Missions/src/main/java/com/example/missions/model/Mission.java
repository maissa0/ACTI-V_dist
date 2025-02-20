package com.example.missions.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomMission;
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateDebut;


    @Enumerated(EnumType.STRING)
    private MissionStatus statut;


    @ElementCollection
    private List<String> competencesRequises;

    private Long evenementId;

    private Long responsableId;
}


