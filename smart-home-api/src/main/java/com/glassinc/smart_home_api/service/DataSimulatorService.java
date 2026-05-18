package com.glassinc.smart_home_api.service;

import com.glassinc.smart_home_api.model.SensorData;
import com.glassinc.smart_home_api.repository.SensorDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class DataSimulatorService {

    private final SensorDataRepository sensorDataRepository;
    private final Random random = new Random();

    public DataSimulatorService(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    // S'exécute toutes les 5000 millisecondes (5 secondes) après la fin de la tâche précédente
    @Scheduled(fixedDelay = 5000)
    public void simulateTemperature() {
        // Génère une température entre 18.0 et 25.0
        float temperature = 18.0f + random.nextFloat() * 7.0f;

        SensorData data = new SensorData();
        data.setDeviceName("Capteur Salon");
        data.setSensorValue(Math.round(temperature * 100.0f) / 100.0f); // Arrondi à 2 décimales
        data.setMeasureTime(LocalDateTime.now());

        sensorDataRepository.save(data);

        System.out.println(">>> SIMULATION : Température sauvegardée -> " + data.getSensorValue() + "°C");
    }
}