package com.example.evaluationvolontaireservice.Services;
import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;
import com.example.evaluationvolontaireservice.Repository.evaluationvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



@Service
public class evaluationvService {
    @Autowired
    private evaluationvRepository repository;

    public evaluationVolontaire saveEvaluation(evaluationVolontaire evaluation) {
        return repository.save(evaluation);
    }

    public evaluationVolontaire deleteEvaluation(String id) {
        Optional<evaluationVolontaire> evaluationOptional = repository.findById(id);
        if (evaluationOptional.isPresent()) {
            evaluationVolontaire evaluation = evaluationOptional.get();
            repository.delete(evaluation);
            return evaluation;
        } else {
            throw new IllegalArgumentException("Évaluation avec l'ID " + id + " introuvable.");
        }
    }

    public evaluationVolontaire updateEvaluation(evaluationVolontaire evaluation) {
        if (repository.existsById(evaluation.getId())) {
            return repository.save(evaluation);  // Mise à jour si l'ID existe
        } else {
            throw new IllegalArgumentException("Évaluation avec l'ID " + evaluation.getId() + " introuvable.");
        }
    }

    public evaluationVolontaire getEvaluationById(String id) {
        Optional<evaluationVolontaire> evaluationOptional = repository.findById(id);
        return evaluationOptional.orElseThrow(() -> new IllegalArgumentException("Évaluation avec l'ID " + id + " introuvable."));
    }

    public List<evaluationVolontaire> getAllEvaluations() {
        return repository.findAll();
    }
}