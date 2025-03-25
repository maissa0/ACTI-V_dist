package com.example.gestionabsences.service;

import com.example.gestionabsences.dto.ClasseDTO;
import com.example.gestionabsences.entity.Classe;

import java.util.List;

public interface ClasseService {
    List<ClasseDTO> getAllClasses();
    List<ClasseDTO> getClassesByNiveau(Integer niveau);
    ClasseDTO getClasseById(Long id);
    ClasseDTO saveClasse(ClasseDTO classeDTO);
    ClasseDTO updateClasse(Long id, ClasseDTO classeDTO);
    void deleteClasse(Long id);
}
