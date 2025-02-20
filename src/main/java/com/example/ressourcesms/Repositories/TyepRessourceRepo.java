package com.example.ressourcesms.Repositories;

import com.example.ressourcesms.Entities.TypeRessources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TyepRessourceRepo extends JpaRepository<TypeRessources,Long> {
}
