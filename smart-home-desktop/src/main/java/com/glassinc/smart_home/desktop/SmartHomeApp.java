package com.glassinc.smart_home.desktop; // Mets ton vrai package ici

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.util.Duration;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SmartHomeApp extends Application {

    private static final String API_BASE_URL = "http://localhost:8080/api";
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public void start(Stage primaryStage) {

        // --- SECTION  CAPTEURS (Haut) ---
        Label sensorTitle = new Label("\uD83D\uDCE1 Capteurs en direct");
        sensorTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // 1. Création du Tableau
        TableView<SensorData> sensorTable = new TableView<>();
        setupSensorTable(sensorTable); // Méthode pour configurer les colonnes

        // --- SECTION APPAREILS (Bas) ---
        Label deviceTitle = new Label("\uD83D\uDCA1 Contrôle des appareils");
        deviceTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        TableView<Device> deviceTable = new TableView<>();
        setupDeviceTable(deviceTable); // Méthode pour configurer les colonnes

        // --- BOUTON DE CONTRÔLE ---
        Button toggleButton = new Button("Allumer / Eteindre l'appareil sélectionné");
        toggleButton.setOnAction(event -> toggleDevicesState(deviceTable));

        // --- MISE EN PAGE ---
        VBox root = new VBox(15, sensorTitle, sensorTable, deviceTitle, deviceTable, toggleButton);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 750, 700);

        primaryStage.setTitle("SmartHome Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

        // --- TEMPS REEL ---
        fetchSensorData(sensorTable);
        fetchDeviceData(deviceTable);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            fetchSensorData(sensorTable);
            fetchDeviceData(deviceTable); // On rafraîchit aussi les appareils au cas où
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // --- METHODES DE CONFIGURATION DES TABLEAUX ---
    private void setupSensorTable(TableView<SensorData> table) {
        TableColumn<SensorData, String> nameCol = new TableColumn<>("Capteur");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        nameCol.setPrefWidth(150);

        TableColumn<SensorData, Number> valueCol = new TableColumn<>("Température (°C)");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("sensorValue"));
        valueCol.setPrefWidth(150);

        TableColumn<SensorData, String> timeCol = new TableColumn<>("Date / Heure");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("measureTime"));
        timeCol.setPrefWidth(300);

        table.getColumns().add(nameCol);
        table.getColumns().add(valueCol);
        table.getColumns().add(timeCol);
        table.setPrefHeight(250);
    }

    private void setupDeviceTable(TableView<Device> table) {
        TableColumn<Device, String> nameCol = new TableColumn<>("Appareil");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Device, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);

        // Colonne spéciale pour l'état (Affichera true ou false)
        TableColumn<Device, Boolean> stateCol = new TableColumn<>("Allumé ?");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateCol.setPrefWidth(100);

        table.getColumns().add(nameCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(stateCol);
        table.setPrefHeight(200);
    }

    // --- METHODES DE RECUPERATION DES DONNEES (GET) ---
    private void fetchSensorData(TableView<SensorData> table) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/sensor-data")).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<SensorData>>(){}.getType();
                    List<SensorData> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> table.getItems().addAll(list));
                });
    }

    private void fetchDeviceData(TableView<Device> table) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/devices")).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<Device>>(){}.getType();
                    List<Device> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> table.getItems().addAll(list));
                });
    }

    // --- METHODE DE CONTRÔLE (PUT) ---
    private void toggleDevicesState(TableView<Device> table) {
        Device selectedDevice = table.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            System.out.println("Aucun appareil séléctionné !");
            return;
        }

        // On inverse l'état
        selectedDevice.setState(!selectedDevice.isState());

        // On convertit l'objet Java en JSON pour l'envoyer au serveur
        String jsonBody = gson.toJson(selectedDevice);

        // Requête PUT vers /api/devices/{id}
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/devices/" + selectedDevice.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenRun(() -> {
                    Platform.runLater(() -> fetchDeviceData(table)); // ON rafraichit le tableau après la mise a jour
                });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
/*
        // Colonnes
        TableColumn<SensorData, String> nameCol = new TableColumn<>("Capteur");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        nameCol.setPrefWidth(150);

        TableColumn<SensorData, Number> valueCol = new TableColumn<>("Température (°C)");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("sensorValue"));
        valueCol.setPrefWidth(150);

        TableColumn<SensorData, String> timeCol = new TableColumn<>("Date / Heure");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("measureTime"));
        timeCol.setPrefWidth(300);

        table.getColumns().add(nameCol);
        table.getColumns().add(valueCol);
        table.getColumns().add(timeCol);

        // 2. Bouton
        Button fetchButton = new Button("Rafraîchir manuellement");
        //fetchButton.setOnAction(e -> fetchSensorData(table));

        // 3.Timeline
        // On crée un minuteur qui se déclenche toutes les 5 secondes
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(5),
                event -> fetchSensorData(table) // Action à éxecuter
        ));
        timeline.setCycleCount(Animation.INDEFINITE); //On le répète a l'infini
        timeline.play(); //On démarre le minuteur

        // Le bouton permet toujours un rafraichissement manuel instantanée
        fetchButton.setOnAction(event -> fetchSensorData(table));

        // 4. Mise en page
        VBox root = new VBox(10, fetchButton, table);
        Scene scene = new Scene(root, 650, 500);

        primaryStage.setTitle("Smart Home Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Premier chargement immédiat au démarrage
        fetchSensorData(table);
    }

    //Méthode de récupération de données ASYNCHRONE
    private void fetchSensorData(TableView<SensorData> table) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            // sendAsync est non-bloquant ! L'interface ne gèle pas.
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(json -> {

                        // --- LA MAGIE DE GSON ---
                        Gson gson = new Gson();
                        // On dit à Gson qu'on veut convertir le JSON en Liste<SensorData>
                        Type listType = new TypeToken<List<SensorData>>(){}.getType();
                        List<SensorData> sensorDataList = gson.fromJson(json, listType);

                        // ETREMEMENT IMPORTANT
                        // On ne peut modifier l'interface graphique que depuis le Thread JavaFx.
                        // Comme on est dans un callback asynchrone (en arrière-plan),
                        // on doit utiliser Platform.runLater() pour dire à JavaFx de faire la mise à jour
                        Platform.runLater(() -> {
                            // On met à jour le tableau avec les nouvelles données
                            table.getItems().setAll(sensorDataList);
                        });
                    });
            //HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception ex) {
            System.err.println("Erreur : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/