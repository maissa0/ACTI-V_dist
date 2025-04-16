package tn.esprit.competences.services;

import tn.esprit.competences.dto.CompetenceRequest;
import tn.esprit.competences.entities.Competence;
import tn.esprit.competences.repositories.CompetenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompetenceService {
    private final CompetenceRepository competenceRepository;

    public CompetenceService(CompetenceRepository competenceRepository) {
        this.competenceRepository = competenceRepository;
    }

    public Competence addCompetence(CompetenceRequest request) {
        Competence competence = new Competence();
        competence.setName(request.getName());
        competence.setDescription(request.getDescription());
        competence.setLevel(request.getLevel());
        competence.setUserId(request.getUserId());
        return competenceRepository.save(competence);
    }

    public List<Competence> getUserCompetences(Long userId) {
        return competenceRepository.findByUserId(userId);
    }

    public void deleteCompetence(Long id, Long userId) {
        Competence competence = competenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competence not found"));
        
        if (!competence.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this competence");
        }
        
        competenceRepository.deleteById(id);
    }

    public Competence updateCompetence(Long id, CompetenceRequest request, Long userId) {
        Competence competence = competenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competence not found"));
        
        if (!competence.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this competence");
        }
        
        competence.setName(request.getName());
        competence.setDescription(request.getDescription());
        competence.setLevel(request.getLevel());
        
        return competenceRepository.save(competence);
    }
} 