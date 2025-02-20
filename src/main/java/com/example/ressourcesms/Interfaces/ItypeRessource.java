package com.example.ressourcesms.Interfaces;

import com.example.ressourcesms.Entities.TypeRessources;

import java.util.List;

public interface ItypeRessource {
     TypeRessources saveType(TypeRessources typeRessources);

    TypeRessources updateType(Long id, TypeRessources typeRessources);

    TypeRessources getTypeById(Long id);

    List<TypeRessources> getAllTypes();

    void deleteType(Long id);
}
