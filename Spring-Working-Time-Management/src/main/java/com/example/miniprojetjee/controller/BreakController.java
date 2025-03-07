package com.example.miniprojetjee.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/break")
public class BreakController {

    // Mocked list of breaks. You can replace this with your actual service logic.
    private List<Break> breaks = new ArrayList<>();

    @PostMapping("/add")
    public ResponseEntity<String> addBreak(@RequestBody Break newBreak) {
        // Logic to add a break
        breaks.add(newBreak);
        return ResponseEntity.ok("Break added");
    }

    @GetMapping("/list")
    public ResponseEntity<List<Break>> getBreaks() {
        // Logic to get all breaks
        return ResponseEntity.ok(breaks);
    }

    public static class Break {
        private String date;
        private String start;
        private String end;

        // Getters and setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }
}

