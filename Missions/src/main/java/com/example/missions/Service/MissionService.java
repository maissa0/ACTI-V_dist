package com.example.missions.Service;

import com.example.missions.Interface.MissionInterface;
import com.example.missions.model.Mission;
import com.example.missions.repositories.MissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MissionService implements MissionInterface {

    private final MissionRepository missionRepository;

    public MissionService(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
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
}
