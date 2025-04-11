package com.example.missions.controller;

import com.example.missions.Interface.EquipeInterface;
import com.example.missions.Interface.MissionInterface;
import com.example.missions.model.Equipe;
import com.example.missions.model.Mission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionInterface missionService;
    private final EquipeInterface equipeService;

    public MissionController(MissionInterface missionService , EquipeInterface equipeService) {
        this.missionService = missionService;
        this.equipeService = equipeService;
    }


    @PostMapping("/create")
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        Mission createdMission = missionService.createMission(mission);
        return ResponseEntity.ok(createdMission);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Mission>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        Optional<Mission> mission = missionService.getMissionById(id);
        return mission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        Mission updatedMission = missionService.updateMission(id, mission);
        return updatedMission != null ? ResponseEntity.ok(updatedMission) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{equipeId}/assign-mission/{missionId}")
    public ResponseEntity<Equipe> assignMissionToEquipe(
            @PathVariable Long equipeId,
            @PathVariable Long missionId) {
          missionService.assignMissionToEquipe(equipeId, missionId);
        return ResponseEntity.ok(equipeService.getEquipeById(equipeId).orElse(null));
    }
}

