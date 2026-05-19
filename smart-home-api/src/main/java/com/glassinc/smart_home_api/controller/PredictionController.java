package com.glassinc.smart_home_api.controller;

import com.glassinc.smart_home_api.model.Prediction;
import com.glassinc.smart_home_api.repository.PredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {
    @Autowired
    private PredictionRepository predictionRepository;

    @GetMapping
    public List<Prediction> getPredictions() {
        return predictionRepository.findAll();
    }

    @PostMapping
    public Prediction createPrediction(@RequestBody Prediction prediction) {
        // On vide l'ancienne prédiction pour n'avoir que la plus récente
        predictionRepository.deleteAll();
        return predictionRepository.save(prediction);
    }
}
