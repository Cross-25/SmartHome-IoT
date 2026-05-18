package com.glassinc.smart_home.desktop;

public class SensorData {
    private Long id;
    private String deviceName;
    private float sensorValue;
    private String measureTime;

    // Getters (Obligatoires pour que JavaFX puisse lire les données pour le tableau)
    public Long getId() { return id; }
    public String getDeviceName() { return deviceName; }
    public float getSensorValue() { return sensorValue; }
    public String getMeasureTime() { return measureTime; }
}