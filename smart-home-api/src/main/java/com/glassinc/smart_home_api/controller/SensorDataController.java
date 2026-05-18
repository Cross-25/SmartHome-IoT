package com.glassinc.smart_home_api.controller;

import com.glassinc.smart_home_api.model.SensorData;
import com.glassinc.smart_home_api.repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Dit à Spring que cette classe gère des requêtes web
@RequestMapping("/api") // L'URL de base sera /api
public class SensorDataController {

    @Autowired
    private SensorDataRepository sensorDataRepository; // L'outil pour lire la base de données

    // Quand on fait un GET sur /api/sensor-data, on exécute cette méthode
    @GetMapping("/sensor-data")
    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll(); // Récupère tout l'historique et le renvoie en JSON
    }
}