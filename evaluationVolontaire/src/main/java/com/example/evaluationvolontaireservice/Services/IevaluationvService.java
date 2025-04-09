package com.example.evaluationvolontaireservice.Services;

import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;

import java.util.List;

public interface IevaluationvService {
    evaluationVolontaire saveEvaluation(evaluationVolontaire evaluation);
    evaluationVolontaire deleteEvaluation(Long id);
    evaluationVolontaire updateEvaluation(evaluationVolontaire evaluation);
    evaluationVolontaire getEvaluationById(Long id);
    List<evaluationVolontaire> getAllEvaluations();
}
