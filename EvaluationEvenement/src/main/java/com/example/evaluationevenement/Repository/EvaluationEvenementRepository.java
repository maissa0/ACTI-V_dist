package com.example.evaluationevenement.Repository;

import com.example.evaluationevenement.Entity.EvaluationEvenement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationEvenementRepository extends MongoRepository<EvaluationEvenement, String> {
    List<EvaluationEvenement> findByNote(int note);
    List<EvaluationEvenement> findAllByOrderByNoteDesc();
}
