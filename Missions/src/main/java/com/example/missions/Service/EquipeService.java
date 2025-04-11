package com.example.missions.Service;

import com.example.missions.Interface.EquipeInterface;
import com.example.missions.model.Equipe;
import com.example.missions.repositories.EquipeRepository;

import java.util.List;
import java.util.Optional;

public class EquipeService implements EquipeInterface {
    EquipeRepository equipeRepository;
    @Override
    public Equipe createEquipe(Equipe equipe) {
        return equipeRepository.save(equipe) ;
    }

    @Override
    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll();
    }

    @Override
    public Optional<Equipe> getEquipeById(Long id) {
        return equipeRepository.findById(id);
    }

    @Override
    public Equipe updateEquipe(Long id, Equipe equipe) {
        return equipeRepository.save(equipe);
    }

    @Override
    public void deleteEquipe(Long id) {

    }
}
