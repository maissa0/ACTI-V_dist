package com.example.missions.repositories;

import com.example.missions.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findByEvenementId(Long evenementId);
}