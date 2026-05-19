package com.glassinc.smart_home_api.model;

import jakarta.persistence.*;

@Entity
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float predictedValue;
    private String targetTime; // "dans 15 minutes"

    // Constructeur vide + Getters/Setters
    public Prediction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public float getPredictedValue() { return predictedValue; }
    public void setPredictedValue(float predictedValue) { this.predictedValue = predictedValue; }
    public String getTargetTime() { return targetTime; }
    public void setTargetTime(String targetTime) { this.targetTime = targetTime; }
}
