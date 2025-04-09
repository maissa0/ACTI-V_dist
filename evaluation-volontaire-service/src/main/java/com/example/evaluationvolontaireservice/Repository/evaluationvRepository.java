package com.example.evaluationvolontaireservice.Repository;

import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface evaluationvRepository extends MongoRepository<evaluationVolontaire, String> {
}
