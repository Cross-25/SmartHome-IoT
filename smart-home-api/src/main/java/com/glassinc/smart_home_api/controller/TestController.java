package com.glassinc.smart_home_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Dit à Spring : "Cette classe gère des requêtes web"
@RequestMapping("/api/test") // L'URL de base sera localhost:8080/api/test
public class TestController {

    @GetMapping // Si quelqu'un fait un "GET" sur cette URL, on exécute cette méthode
    public String helloDomotique() {
        return "Bienvenue dans le backend de ta Smart Home ! 🏠";
    }
}
