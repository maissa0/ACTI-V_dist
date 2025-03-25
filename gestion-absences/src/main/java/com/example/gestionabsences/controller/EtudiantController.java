package com.example.gestionabsences.controller;

import com.example.gestionabsences.dto.EtudiantDTO;
import com.example.gestionabsences.entity.Etudiant;
import com.example.gestionabsences.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {
    private final EtudiantService etudiantService;

    @Autowired
    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public ResponseEntity<List<EtudiantDTO>> getAllEtudiants() {
        return ResponseEntity.ok(etudiantService.getAllEtudiants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtudiantDTO> getEtudiantById(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getEtudiantById(id));
    }

    @GetMapping("/classe/{classeId}")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsByClasseId(@PathVariable Long classeId) {
        return ResponseEntity.ok(etudiantService.getEtudiantsByClasseId(classeId));
    }

    @PostMapping
    public ResponseEntity<EtudiantDTO> createEtudiant(@Valid @RequestBody EtudiantDTO etudiantDTO) {
        return new ResponseEntity<>(etudiantService.saveEtudiant(etudiantDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EtudiantDTO> updateEtudiant(@PathVariable Long id, @RequestBody EtudiantDTO etudiantDTO) {
        return ResponseEntity.ok(etudiantService.updateEtudiant(id, etudiantDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable Long id) {
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }
}
