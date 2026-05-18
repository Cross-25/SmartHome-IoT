package com.glassinc.smart_home.desktop; // Mets ton vrai package ici

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class SmartHomeApp extends Application {

    private static final String API_URL = "http://localhost:8080/api/sensor-data";

    @Override
    public void start(Stage primaryStage) {

        // 1. Création du Tableau
        TableView<SensorData> table = new TableView<>();

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
        Button fetchButton = new Button("Rafraîchir les capteurs");
        fetchButton.setOnAction(e -> fetchSensorData(table));

        // 3. Mise en page
        VBox root = new VBox(10, fetchButton, table);
        Scene scene = new Scene(root, 650, 500);

        primaryStage.setTitle("Smart Home Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Charger les données au démarrage
        fetchSensorData(table);
    }

    private void fetchSensorData(TableView<SensorData> table) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // --- LA MAGIE DE GSON ---
            Gson gson = new Gson();
            // On dit à Gson qu'on veut convertir le JSON en Liste<SensorData>
            Type listType = new TypeToken<List<SensorData>>(){}.getType();
            List<SensorData> sensorDataList = gson.fromJson(response.body(), listType);

            // On met à jour le tableau avec les nouvelles données
            table.getItems().setAll(sensorDataList);

        } catch (Exception ex) {
            System.err.println("Erreur : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}