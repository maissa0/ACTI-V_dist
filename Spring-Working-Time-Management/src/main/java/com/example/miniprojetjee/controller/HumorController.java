package com.example.miniprojetjee.controller;

import com.example.miniprojetjee.entity.Humor;

import com.example.miniprojetjee.services.HumorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/humor")
public class HumorController {

    @Autowired
    private HumorService humorService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitHumor(@RequestBody Humor humor) {
        try {
            humorService.saveHumor(humor);
            return ResponseEntity.ok("Humor submitted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to submit humor");
        }
    }
}
