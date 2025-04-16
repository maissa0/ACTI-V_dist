package com.example.evaluationvolontaireservice.Repository;

import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface evaluationvRepository extends MongoRepository<evaluationVolontaire, String> {
    List<evaluationVolontaire> findByNote(int note);
    List<evaluationVolontaire> findAllByOrderByNoteDesc();
    List<evaluationVolontaire> findByUserId(Long userId);
    List<evaluationVolontaire> findByEvenementId(Long evenementId);
    List<evaluationVolontaire> findByEvenementIdAndUserId(Long evenementId, Long userId);
}
