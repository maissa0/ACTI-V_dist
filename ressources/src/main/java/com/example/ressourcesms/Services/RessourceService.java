package com.example.ressourcesms.Services;


import com.example.ressourcesms.Entities.Ressource;
import com.example.ressourcesms.Interfaces.IRessources;
import com.example.ressourcesms.Repositories.RessourceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RessourceService implements IRessources {
    @Autowired
    private RessourceRepo ressourceRepo;

    @Override
    public Ressource saveRessource(Ressource ressource) {
        return ressourceRepo.save(ressource);
    }

    @Override
    public Ressource deleteRessource(int id) {
        Optional<Ressource> ressourceOptional = ressourceRepo.findById(id);

        if (ressourceOptional.isPresent()) {
            Ressource candidat = ressourceOptional.get();
            ressourceRepo.delete(candidat);
            return candidat;
        } else {
            throw new IllegalArgumentException("Ressource with id " + id + " not found.");
        }
    }

    @Override
    public Ressource updateRessource(Ressource ressource) {
        if (ressourceRepo.existsById(ressource.getId())) {
            return ressourceRepo.save(ressource);  // Save the updated Candidat, ID remains unchanged
        } else {
            throw new IllegalArgumentException("Candidat with id " + ressource.getId() + " not found.");
        }    }

    @Override
    public Ressource getRessourceById(int id) {
        Optional<Ressource> ressourceOptional = ressourceRepo.findById(id);
        return ressourceOptional.orElseThrow(() -> new IllegalArgumentException("ressource with id " + id + " not found."));
    }

    @Override
    public List<Ressource> getAllRessources() {
        return ressourceRepo.findAll();
    }

    @Override
    public List<Ressource> getRessourcesByEventId(Long eventId) {
        return ressourceRepo.findByEventId(eventId);
    }
}

