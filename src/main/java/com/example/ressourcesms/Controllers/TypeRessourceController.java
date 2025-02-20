package com.example.ressourcesms.Controllers;


import com.example.ressourcesms.Entities.TypeRessources;
import com.example.ressourcesms.Interfaces.ItypeRessource;
import com.example.ressourcesms.Repositories.TyepRessourceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/TypeRessource")
@RequiredArgsConstructor
public class TypeRessourceController {
    private final TyepRessourceRepo typeRessourcesRepo;

    @GetMapping
    public List<TypeRessources> getAllTypes() {
        return typeRessourcesRepo.findAll();
    }

    @PostMapping
    public TypeRessources createType(@RequestBody TypeRessources type) {
        return typeRessourcesRepo.save(type);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeRessources> updateType(@PathVariable Long id, @RequestBody TypeRessources typeDetails) {
        return typeRessourcesRepo.findById(id)
                .map(type -> {
                    type.setNom(typeDetails.getNom());
                    return ResponseEntity.ok(typeRessourcesRepo.save(type));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        if (typeRessourcesRepo.existsById(id)) {
            typeRessourcesRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
