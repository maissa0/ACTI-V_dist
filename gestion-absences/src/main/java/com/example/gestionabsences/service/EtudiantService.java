package com.example.gestionabsences.service;

import com.example.gestionabsences.dto.EtudiantDTO;
import com.example.gestionabsences.entity.Etudiant;

import java.util.List;

public interface EtudiantService {
    List<EtudiantDTO> getAllEtudiants();
    List<EtudiantDTO> getEtudiantsByClasseId(Long classeId);
    EtudiantDTO getEtudiantById(Long id);
    EtudiantDTO saveEtudiant(EtudiantDTO etudiantDTO);
    EtudiantDTO updateEtudiant(Long id, EtudiantDTO etudiantDTO);
    void deleteEtudiant(Long id);
}
