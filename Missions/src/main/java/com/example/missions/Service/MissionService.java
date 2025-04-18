package com.example.missions.Service;

import com.example.missions.Interface.MissionInterface;
import com.example.missions.model.Equipe;
import com.example.missions.model.Mission;
import com.example.missions.repositories.EquipeRepository;
import com.example.missions.repositories.MissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MissionService implements MissionInterface {
    private final MissionRepository missionRepository;
    private final EquipeRepository equipeRepository;

   

    public MissionService(MissionRepository missionRepository, EquipeRepository equipeRepository) {
        this.missionRepository = missionRepository;
        this.equipeRepository = equipeRepository;
    }

    @Override
    public Mission createMission(Mission mission) {
        return missionRepository.save(mission);
    }

    @Override
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    @Override
    public Optional<Mission> getMissionById(Long id) {
        return missionRepository.findById(id);
    }

    @Override
    public Mission updateMission(Long id, Mission mission) {
        if (missionRepository.existsById(id)) {
            mission.setId(id);
            return missionRepository.save(mission);
        }
        return null;
    }

    @Override
    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    @Override
    public void assignMissionToEquipe(Long equipeId, Long missionId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        equipe.setMission(mission);
        equipeRepository.save(equipe);
    }

    @Override
    public List<Mission> getMissionsByEventId(Long eventId) {
        return missionRepository.findByEvenementId(eventId);
    }
}
