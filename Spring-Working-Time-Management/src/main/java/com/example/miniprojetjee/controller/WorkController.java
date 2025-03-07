package com.example.miniprojetjee.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/work")
public class WorkController {

    // Mocked work status. You can replace this with your actual service logic.
    private boolean isWorkStarted = false;

    @GetMapping("/exist")
    public ResponseEntity<Boolean> checkIfWorkExists() {
        // Logic to check if work exists for the day.
        boolean workExists = isWorkStarted;  // Mocked logic
        return ResponseEntity.ok(workExists);
    }

    @PostMapping("/start")
    public ResponseEntity<String> startWork() {
        // Logic to start work.
        isWorkStarted = true;
        return ResponseEntity.ok("Work started");
    }

    @PostMapping("/end")
    public ResponseEntity<String> endWork() {
        // Logic to end work.
        isWorkStarted = false;
        return ResponseEntity.ok("Work ended");
    }

    @GetMapping("/current-date")
    public ResponseEntity<String> getCurrentDate() {
        // Return current date
        String currentDate = LocalDate.now().toString();
        return ResponseEntity.ok(currentDate);
    }
}
