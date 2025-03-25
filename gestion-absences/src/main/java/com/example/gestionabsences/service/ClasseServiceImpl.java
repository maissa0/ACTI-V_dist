package com.example.gestionabsences.service;

import com.example.gestionabsences.dto.ClasseDTO;
import com.example.gestionabsences.entity.Classe;
import com.example.gestionabsences.repository.ClasseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClasseServiceImpl implements ClasseService {
    private final ClasseRepository classeRepository;

    @Autowired
    public ClasseServiceImpl(ClasseRepository classeRepository) {
        this.classeRepository = classeRepository;
    }

    @Override
    public List<ClasseDTO> getAllClasses() {
        List<Classe> classes = classeRepository.findAll();
        return classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClasseDTO> getClassesByNiveau(Integer niveau) {
        List<Classe> classes = classeRepository.findByNiveau(niveau);
        return classes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClasseDTO getClasseById(Long id) {
        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'id : " + id));
        return convertToDTO(classe);
    }

    @Override
    @Transactional
    public ClasseDTO saveClasse(ClasseDTO classeDTO) {
        Classe classe = new Classe();
        classe.setNom(classeDTO.getNom());
        classe.setNiveau(classeDTO.getNiveau());

        Classe savedClasse = classeRepository.save(classe);
        return convertToDTO(savedClasse);
    }

    @Override
    @Transactional
    public ClasseDTO updateClasse(Long id, ClasseDTO classeDTO) {
        Classe classe = classeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée avec l'id : " + id));

        classe.setNom(classeDTO.getNom());
        classe.setNiveau(classeDTO.getNiveau());

        Classe updatedClasse = classeRepository.save(classe);
        return convertToDTO(updatedClasse);
    }

    @Override
    @Transactional
    public void deleteClasse(Long id) {
        classeRepository.deleteById(id);
    }

    private ClasseDTO convertToDTO(Classe classe) {
        ClasseDTO dto = new ClasseDTO();
        dto.setId(classe.getId());
        dto.setNom(classe.getNom());
        dto.setNiveau(classe.getNiveau());
        if (classe.getEtudiants() != null) {
            dto.setEtudiants(classe.getEtudiants().stream()
                    .map(etudiant -> etudiant.getId())
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private Classe toEntity(ClasseDTO dto) {
        Classe classe = new Classe();
        classe.setId(dto.getId());
        classe.setNom(dto.getNom());
        classe.setNiveau(dto.getNiveau());
        return classe;
    }
}