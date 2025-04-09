package com.example.evaluationvolontaireservice.Controllers;

import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;
import com.example.evaluationvolontaireservice.Services.evaluationvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluationVolontaire")
public class evaluationvRestController {

    @Autowired
    private evaluationvService evaluationService;

    @GetMapping("/list")
    public ResponseEntity<List<evaluationVolontaire>> getAllEvaluations() {
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<evaluationVolontaire> getEvaluationById(@PathVariable String id) {
        return ResponseEntity.ok(evaluationService.getEvaluationById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<evaluationVolontaire> createEvaluation(@RequestBody evaluationVolontaire evaluation) {
        return ResponseEntity.ok(evaluationService.saveEvaluation(evaluation));
    }

    @PutMapping("/update")
    public ResponseEntity<evaluationVolontaire> updateEvaluation(@RequestBody evaluationVolontaire evaluation) {
        return ResponseEntity.ok(evaluationService.updateEvaluation(evaluation));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<evaluationVolontaire> deleteEvaluation(@PathVariable String id) {
        return ResponseEntity.ok(evaluationService.deleteEvaluation(id));
    }

    @GetMapping("/note/{note}")
    public ResponseEntity<List<evaluationVolontaire>> getEvaluationsByNote(@PathVariable int note) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByNote(note));
    }

    @GetMapping("/note/highest")
    public ResponseEntity<List<evaluationVolontaire>> getEvaluationsOrderedByNoteDesc() {
        return ResponseEntity.ok(evaluationService.getEvaluationsOrderedByNoteDesc());
    }
}
