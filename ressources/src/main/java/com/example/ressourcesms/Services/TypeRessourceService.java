package com.example.ressourcesms.Services;

import com.example.ressourcesms.Entities.TypeRessources;
import com.example.ressourcesms.Interfaces.ItypeRessource;
import com.example.ressourcesms.Repositories.TyepRessourceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TypeRessourceService implements ItypeRessource {

    @Autowired
    private static TyepRessourceRepo typRessourceRepo;
    @Override
    public  TypeRessources saveType(TypeRessources typeRessources) {
        return typRessourceRepo.save(typeRessources);
    }

    @Override
    public TypeRessources updateType(Long id, TypeRessources typeRessources) {
        if (typRessourceRepo.existsById(typeRessources.getId())) {
            return typRessourceRepo.save(typeRessources);  // Save the updated Candidat, ID remains unchanged
        } else {
            throw new IllegalArgumentException("typeRessource with id " + typeRessources.getId() + " not found.");
        }    }

    @Override
    public TypeRessources getTypeById(Long id) {
        Optional<TypeRessources> TressourceOptional = typRessourceRepo.findById(id);
        return TressourceOptional.orElseThrow(() -> new IllegalArgumentException("Typeressource with id " + id + " not found."));
    }

    @Override
    public List<TypeRessources> getAllTypes() {
        return typRessourceRepo.findAll();

    }

    @Override
    public void deleteType(Long id) {

        if (typRessourceRepo.existsById(id)) {
            typRessourceRepo.deleteById(id);
        } else {
            throw new IllegalArgumentException("TypeRessource with ID " + id + " not found.");
        }
    }
}
