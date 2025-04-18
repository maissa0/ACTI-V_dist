package com.example.missions.Interface;

import com.example.missions.model.Mission;

import java.util.List;
import java.util.Optional;

public interface MissionInterface {

    Mission createMission(Mission mission);

    List<Mission> getAllMissions();

    Optional<Mission> getMissionById(Long id);

    Mission updateMission(Long id, Mission mission);

    void deleteMission(Long id);

    void assignMissionToEquipe(Long equipeId, Long missionId);

    List<Mission> getMissionsByEventId(Long eventId);
}

