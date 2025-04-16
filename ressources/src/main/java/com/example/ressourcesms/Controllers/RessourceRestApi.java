package com.example.ressourcesms.Controllers;

import com.example.ressourcesms.Entities.Ressource;
import com.example.ressourcesms.Services.RessourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ressources")
@RequiredArgsConstructor
public class RessourceRestApi {
    @Autowired
    private final RessourceService ressourceService;

    private String title="Hello, i'm the Ressource Micro-Service";
    @RequestMapping("/hello")
    public String sayHello(){
        System.out.println(title);
        return title;
    }
    @PostMapping
    public ResponseEntity<Ressource> createRessource(@RequestBody Ressource ressource) {
        return ResponseEntity.ok(ressourceService.saveRessource(ressource));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ressource> getRessourceById(@PathVariable int id) {
        return ResponseEntity.ok(ressourceService.getRessourceById(id));
    }

    @GetMapping
    public ResponseEntity<List<Ressource>> getAllRessources() {
        return ResponseEntity.ok(ressourceService.getAllRessources());
    }
    
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Ressource>> getRessourcesByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(ressourceService.getRessourcesByEventId(eventId));
    }

    @PutMapping
    public ResponseEntity<Ressource> updateRessource(@RequestBody Ressource ressource) {
        return ResponseEntity.ok(ressourceService.updateRessource(ressource));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Ressource> deleteRessource(@PathVariable int id) {
        return ResponseEntity.ok(ressourceService.deleteRessource(id));
    }
}
