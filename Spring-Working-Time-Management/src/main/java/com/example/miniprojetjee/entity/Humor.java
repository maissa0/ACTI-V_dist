package com.example.miniprojetjee.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Humor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String humor;
    private Date date;
    private int rate;

    public int getRating() {
        return rate;
    }

    // Setter for rating
    public void setRating(int rating) {
        this.rate = rating;
    }

    // Getter for humor
    public String getHumor() {
        return humor;
    }
}
