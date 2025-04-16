package com.example.missions.Interface;

import com.example.missions.model.Equipe;
import com.example.missions.model.Mission;

import java.util.List;
import java.util.Optional;

public interface EquipeInterface {

    Equipe createEquipe(Equipe equipe);

    List<Equipe> getAllEquipes();

    Optional<Equipe> getEquipeById(Long id);

    Equipe updateEquipe(Long id, Equipe equipe);

    void deleteEquipe(Long id);

    Equipe assignVolontaireToEquipe(Long equipeId, Long volontaireId);


}

