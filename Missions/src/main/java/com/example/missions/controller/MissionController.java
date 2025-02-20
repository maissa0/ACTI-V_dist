package com.example.missions.controller;

import com.example.missions.Interface.MissionInterface;
import com.example.missions.model.Mission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionInterface missionService;

    public MissionController(MissionInterface missionService) {
        this.missionService = missionService;
    }

    // Create a new mission
    @PostMapping("/create")
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        Mission createdMission = missionService.createMission(mission);
        return ResponseEntity.ok(createdMission);
    }

    // Get all missions
    @GetMapping("/all")
    public ResponseEntity<List<Mission>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }

    // Get a mission by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        Optional<Mission> mission = missionService.getMissionById(id);
        return mission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a mission by ID
    @PutMapping("/update/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        Mission updatedMission = missionService.updateMission(id, mission);
        return updatedMission != null ? ResponseEntity.ok(updatedMission) : ResponseEntity.notFound().build();
    }

    // Delete a mission by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }
}

