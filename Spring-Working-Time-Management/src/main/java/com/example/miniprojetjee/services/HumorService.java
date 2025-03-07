package com.example.miniprojetjee.services;


import com.example.miniprojetjee.entity.Humor;
import org.springframework.stereotype.Service;

@Service
public class HumorService {

    // This is just a mock of the save operation, you would normally use a database
    public void saveHumor(Humor humor) {
        // Logic to save the humor (e.g., in a database)
        System.out.println("Humor saved: " + humor.getHumor() + " with rating: " + humor.getRating());
    }
}
