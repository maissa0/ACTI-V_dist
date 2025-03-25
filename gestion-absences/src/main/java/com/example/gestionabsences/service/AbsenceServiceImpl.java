package com.example.gestionabsences.service;

import com.example.gestionabsences.dto.AbsenceDTO;
import com.example.gestionabsences.entity.Absence;
import com.example.gestionabsences.entity.Etudiant;
import com.example.gestionabsences.repository.AbsenceRepository;
import com.example.gestionabsences.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbsenceServiceImpl implements AbsenceService {
    private final AbsenceRepository absenceRepository;
    private final EtudiantRepository etudiantRepository;

    @Autowired
    public AbsenceServiceImpl(AbsenceRepository absenceRepository, EtudiantRepository etudiantRepository) {
        this.absenceRepository = absenceRepository;
        this.etudiantRepository = etudiantRepository;
    }

    private AbsenceDTO toDTO(Absence absence) {
        AbsenceDTO dto = new AbsenceDTO();
        dto.setId(absence.getId());
        dto.setDate(absence.getDate());
        dto.setRaison(absence.getRaison());
        dto.setJustifiee(absence.isJustifiee());
        dto.setEtudiantId(absence.getEtudiant().getId());
        return dto;
    }

    private Absence toEntity(AbsenceDTO dto, Etudiant etudiant) {
        Absence absence = new Absence();
        absence.setDate(dto.getDate());
        absence.setRaison(dto.getRaison());
        absence.setJustifiee(dto.isJustifiee());
        absence.setEtudiant(etudiant);
        return absence;
    }

    @Override
    public List<AbsenceDTO> getAllAbsences() {
        return absenceRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AbsenceDTO getAbsenceById(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'id : " + id));
        return toDTO(absence);
    }

    @Override
    public List<AbsenceDTO> getAbsencesByEtudiantId(Long etudiantId) {
        return absenceRepository.findByEtudiantId(etudiantId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AbsenceDTO> getAbsencesByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        return absenceRepository.findByDateBetween(dateDebut, dateFin).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AbsenceDTO> getAbsencesByJustifiee(boolean justifiee) {
        return absenceRepository.findByJustifiee(justifiee).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AbsenceDTO saveAbsence(AbsenceDTO absenceDTO, Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + etudiantId));

        Absence absence = toEntity(absenceDTO, etudiant);
        Absence savedAbsence = absenceRepository.save(absence);
        return toDTO(savedAbsence);
    }

    @Override
    @Transactional
    public AbsenceDTO updateAbsence(Long id, AbsenceDTO absenceDTO) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée avec l'id : " + id));

        absence.setDate(absenceDTO.getDate());
        absence.setRaison(absenceDTO.getRaison());
        absence.setJustifiee(absenceDTO.isJustifiee());
        Absence updatedAbsence = absenceRepository.save(absence);
        return toDTO(updatedAbsence);
    }

    @Override
    @Transactional
    public void deleteAbsence(Long id) {
        absenceRepository.deleteById(id);
    }
}
