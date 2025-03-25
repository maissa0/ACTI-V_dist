package com.example.gestionabsences.service;

import com.example.gestionabsences.dto.EtudiantDTO;
import com.example.gestionabsences.entity.Classe;
import com.example.gestionabsences.entity.Etudiant;
import com.example.gestionabsences.repository.ClasseRepository;
import com.example.gestionabsences.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtudiantServiceImpl implements EtudiantService {
    private final EtudiantRepository etudiantRepository;
    private final ClasseRepository classeRepository;

    @Autowired
    public EtudiantServiceImpl(EtudiantRepository etudiantRepository, ClasseRepository classeRepository) {
        this.etudiantRepository = etudiantRepository;
        this.classeRepository = classeRepository;
    }

    @Override
    public List<EtudiantDTO> getAllEtudiants() {
        return etudiantRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EtudiantDTO> getEtudiantsByClasseId(Long classeId) {
        return etudiantRepository.findByClasseId(classeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EtudiantDTO getEtudiantById(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + id));
        return convertToDTO(etudiant);
    }

    @Override
    @Transactional
    public EtudiantDTO saveEtudiant(EtudiantDTO etudiantDTO) {
        Classe classe = classeRepository.findById(etudiantDTO.getClasseId())
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'id : " + etudiantDTO.getClasseId()));

        Etudiant etudiant = new Etudiant();
        etudiant.setNom(etudiantDTO.getNom());
        etudiant.setPrenom(etudiantDTO.getPrenom());
        etudiant.setDateNaissance(etudiantDTO.getDateNaissance());
        etudiant.setClasse(classe);

        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        return convertToDTO(savedEtudiant);
    }

    @Override
    @Transactional
    public EtudiantDTO updateEtudiant(Long id, EtudiantDTO etudiantDTO) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + id));

        etudiant.setNom(etudiantDTO.getNom());
        etudiant.setPrenom(etudiantDTO.getPrenom());
        etudiant.setDateNaissance(etudiantDTO.getDateNaissance());

        if (etudiantDTO.getClasseId() != null) {
            Classe classe = classeRepository.findById(etudiantDTO.getClasseId())
                    .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'id : " + etudiantDTO.getClasseId()));
            etudiant.setClasse(classe);
        }

        Etudiant updatedEtudiant = etudiantRepository.save(etudiant);
        return convertToDTO(updatedEtudiant);
    }

    @Override
    @Transactional
    public void deleteEtudiant(Long id) {
        etudiantRepository.deleteById(id);
    }

    private EtudiantDTO convertToDTO(Etudiant etudiant) {
        EtudiantDTO dto = new EtudiantDTO();
        dto.setId(etudiant.getId());
        dto.setNom(etudiant.getNom());
        dto.setPrenom(etudiant.getPrenom());
        dto.setDateNaissance(etudiant.getDateNaissance());
        dto.setClasseId(etudiant.getClasse().getId());
        if (etudiant.getAbsences() != null) {
            dto.setAbsences(etudiant.getAbsences().stream()
                    .map(absence -> absence.getId())
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
