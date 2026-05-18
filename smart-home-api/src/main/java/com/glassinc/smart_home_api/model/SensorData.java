package com.glassinc.smart_home_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceName; // Ex: "Capteur Salon"
    private float sensorValue;      // Ex: 22.5
    private LocalDateTime measureTime; // La date de la mesure

    public SensorData() {}

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public float getSensorValue() { return sensorValue; }
    public void setSensorValue(float sensorValue) { this.sensorValue = sensorValue; }

    public LocalDateTime getMeasureTime() { return measureTime; }
    public void setMeasureTime(LocalDateTime measureTime) { this.measureTime = measureTime; }
}