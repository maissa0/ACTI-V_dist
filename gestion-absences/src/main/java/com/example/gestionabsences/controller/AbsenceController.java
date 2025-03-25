package com.example.gestionabsences.controller;

import com.example.gestionabsences.dto.AbsenceDTO;
import com.example.gestionabsences.service.AbsenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/absences")
public class AbsenceController {

    private final AbsenceService absenceService;

    @Autowired
    public AbsenceController(AbsenceService absenceService) {
        this.absenceService = absenceService;
    }

    @GetMapping
    public ResponseEntity<List<AbsenceDTO>> getAllAbsences() {
        return ResponseEntity.ok(absenceService.getAllAbsences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbsenceDTO> getAbsenceById(@PathVariable Long id) {
        return ResponseEntity.ok(absenceService.getAbsenceById(id));
    }

    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<AbsenceDTO>> getAbsencesByEtudiantId(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(absenceService.getAbsencesByEtudiantId(etudiantId));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AbsenceDTO>> getAbsencesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return ResponseEntity.ok(absenceService.getAbsencesByDateRange(dateDebut, dateFin));
    }

    @GetMapping("/justifiee")
    public ResponseEntity<List<AbsenceDTO>> getAbsencesByJustifiee(@RequestParam boolean justifiee) {
        return ResponseEntity.ok(absenceService.getAbsencesByJustifiee(justifiee));
    }

    @PostMapping
    public ResponseEntity<AbsenceDTO> createAbsence(@Valid @RequestBody AbsenceDTO absenceDTO) {
        AbsenceDTO createdAbsence = absenceService.saveAbsence(absenceDTO, absenceDTO.getEtudiantId());
        return new ResponseEntity<>(createdAbsence, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<AbsenceDTO> updateAbsence(@PathVariable Long id, @RequestBody AbsenceDTO absenceDTO) {
        AbsenceDTO updatedAbsence = absenceService.updateAbsence(id, absenceDTO);
        return ResponseEntity.ok(updatedAbsence);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }
}
