package com.example.missions.Service;

import com.example.missions.Interface.EquipeInterface;
import com.example.missions.model.Equipe;
import com.example.missions.model.Volontaire;
import com.example.missions.repositories.EquipeRepository;
import com.example.missions.repositories.MissionRepository;
import com.example.missions.repositories.VolontaireRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class EquipeService implements EquipeInterface {
   private final EquipeRepository equipeRepository;
 private final VolontaireRepository volontaireRepository;

    private final MailService mailService;


    public EquipeService(EquipeRepository equipeRepository , VolontaireRepository volontaireRepository,MailService mailService) {
        this.equipeRepository = equipeRepository;
        this.volontaireRepository = volontaireRepository;
        this.mailService=mailService;
    }
    @Override
    public Equipe createEquipe(Equipe equipe) {
        return equipeRepository.save(equipe) ;
    }

    @Override
    public List<Equipe> getAllEquipes() {
        return equipeRepository.findAll();
    }

    @Override
    public Optional<Equipe> getEquipeById(Long id) {
        return equipeRepository.findById(id);
    }

    @Override
    public Equipe updateEquipe(Long id, Equipe equipe) {
        if (equipeRepository.existsById(id)) {
            equipe.setId(id);
            return equipeRepository.save(equipe);
        }
        return null;
    }

    @Override
    public void deleteEquipe(Long id) {equipeRepository.deleteById(id);};


    public Equipe assignVolontaireToEquipe(Long equipeId, Long volontaireId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        Volontaire volontaire = volontaireRepository.findById(volontaireId)
                .orElseThrow(() -> new RuntimeException("Volontaire not found"));

        if (!equipe.getMembres().contains(volontaire)) {
            equipe.getMembres().add(volontaire);
            equipeRepository.save(equipe);

            String subject = "Ajout à l'équipe " + equipe.getNomEquipe();
            String content = "Bonjour " + volontaire.getPrenom() + ",\n\n" +
                    "Vous avez été ajouté à l'équipe \"" + equipe.getNomEquipe() +
                    "Merci pour votre engagement !";

            mailService.sendNotificationEmail(volontaire.getEmail(), subject, content);
        }

        return equipe;
    }

}
