package com.example.missions.controller;

import com.example.missions.Interface.EquipeInterface;
import com.example.missions.model.Equipe;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipe")
public class EquipeController {
    EquipeInterface equipeInterface;

    @PostMapping("/create")
    public Equipe createEquipe(@RequestBody Equipe equipe) {
        return equipeInterface.createEquipe(equipe);
    }

    @GetMapping("/all")
    public List<Equipe> getAllEquipes() {
        return equipeInterface.getAllEquipes();
    }

    @GetMapping("/get/{id}")
    public Optional<Equipe> getEquipeById(@PathVariable Long id) {
        return equipeInterface.getEquipeById(id);
    }

    @PutMapping("/update/{id}")
    public Equipe updateEquipe(@PathVariable Long id, @RequestBody Equipe equipe) {
        return equipeInterface.updateEquipe(id, equipe);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEquipe(@PathVariable Long id) {
        equipeInterface.deleteEquipe(id);
    }


}
