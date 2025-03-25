package com.example.gestionabsences.controller;

import com.example.gestionabsences.dto.ClasseDTO;
import com.example.gestionabsences.entity.Classe;
import com.example.gestionabsences.service.ClasseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClasseController {
    private final ClasseService classeService;

    @Autowired
    public ClasseController(ClasseService classeService) {
        this.classeService = classeService;
    }

    @GetMapping
    public ResponseEntity<List<ClasseDTO>> getAllClasses() {
        return ResponseEntity.ok(classeService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClasseDTO> getClasseById(@PathVariable Long id) {
        return ResponseEntity.ok(classeService.getClasseById(id));
    }

    @GetMapping("/niveau/{niveau}")
    public ResponseEntity<List<ClasseDTO>> getClassesByNiveau(@PathVariable Integer niveau) {
        return ResponseEntity.ok(classeService.getClassesByNiveau(niveau));
    }

    @PostMapping
    public ResponseEntity<ClasseDTO> createClasse(@Valid @RequestBody ClasseDTO classeDTO) {
        return new ResponseEntity<>(classeService.saveClasse(classeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClasseDTO> updateClasse(@PathVariable Long id, @RequestBody ClasseDTO classeDTO) {
        return ResponseEntity.ok(classeService.updateClasse(id, classeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClasse(@PathVariable Long id) {
        classeService.deleteClasse(id);
        return ResponseEntity.noContent().build();
    }
}