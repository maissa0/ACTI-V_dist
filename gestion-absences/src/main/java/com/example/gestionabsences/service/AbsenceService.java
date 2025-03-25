package com.example.gestionabsences.service;

import com.example.gestionabsences.dto.AbsenceDTO;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceService {
    List<AbsenceDTO> getAllAbsences();
    AbsenceDTO getAbsenceById(Long id);
    List<AbsenceDTO> getAbsencesByEtudiantId(Long studentId);

    List<AbsenceDTO> getAbsencesByDateRange(LocalDate dateDebut, LocalDate dateFin);
    List<AbsenceDTO> getAbsencesByJustifiee(boolean justifiee);

    AbsenceDTO saveAbsence(AbsenceDTO absence, Long etudiantId);
    AbsenceDTO updateAbsence(Long id, AbsenceDTO absence);
    void deleteAbsence(Long id);
}
