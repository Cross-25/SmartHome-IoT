package com.glassinc.smart_home_api.model;

import jakarta.persistence.*;

@Entity //Dit à Spring : "Crée une table 'device' dans la base de données"
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //Ex: "Lampe Salon"
    private String type; // Ex: "LUMIERE", "CAPTEUR_TEMP"
    private boolean state; // true = allumé, false = éteint

    // Constructeur vide obligatoire pour que Spring/JPA puisse fonctionner
    public Device() {}

    // Constructeur pratique pour créer des appareils facilement
    public Device(String name, String type, boolean state) {
        this.name = name;
        this.type = type;
        this.state = state;
    }

    //GETTERS ET SETTERS
    // (Très important ! Spring les utilise pour lire/écrire les données)

    public long getId() { return id; }
    public void setId(long id) { this.id = id;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isState() { return state; }
    public void setState(boolean on) { this.state = on; }
}
