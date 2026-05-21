package com.glassinc.smart_home.desktop; // Mets ton vrai package ici

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SmartHomeApp extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/dashboard.fxml")
        );

        Scene scene = new Scene(loader.load(), 1100, 750);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/dark-theme.css")
        ).toExternalForm());

        primaryStage.setTitle("SmartHome Dashboard");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
/*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;

import javafx.scene.Scene;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;


public class SmartHomeApp extends Application {

    private static final String API_BASE_URL = "http://localhost:8080/api";
    private final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();

    // Séries pour le graphique (doivent être des attributs de classe pour être mis à jour)
    private XYChart.Series<Number, Number> historySeries;
    private XYChart.Series<Number, Number> predictionSeries;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // --- Section prédiction IA ---
        Label predictionLabel = new Label("Prédiction IA : Calcul en cours...");
        predictionLabel.getStyleClass().add("prediction-label"); // Lien avec le CSS

        // --- SECTION GRAPHIQUE ---
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Temps (derniers points)");
        xAxis.setTickMarkVisible(false); // Cache les numéros moches

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Température (°C)");

        LineChart<Number, Number> tempChart = new LineChart<>(xAxis, yAxis);
        tempChart.setTitle("Historique et Prédiction de Température");
        tempChart.setAnimated(false); // Evite les saccades lors du refresh
        tempChart.setPrefHeight(250);
        tempChart.setCreateSymbols(false); // Enlève-les poinsts sur les lignes pour plus de fluidité

        historySeries = new XYChart.Series<>();
        historySeries.setName("Hisorique Capteur");

        predictionSeries = new XYChart.Series<>();
        predictionSeries.setName("Prediction IA");

        tempChart.getData().add(historySeries);
        tempChart.getData().add(predictionSeries);

        // --- SECTION TABLEAUX ---
        // Capteurs
        TableView<SensorData> sensorTable = new TableView<>();
        setupSensorTable(sensorTable);

        //Appareils
        Label deviceTitle = new Label("Contrôle des appareils");
        HBox deviceHeader = new HBox(10, deviceTitle);

        TableView<Device> deviceTable = new TableView<>();
        setupDeviceTable(deviceTable);

        Button toggleButton = new Button("Allumer / Eteindre");
        toggleButton.setOnAction(e -> toggleDeviceState(deviceTable));

        // --- MISE EN PAGE
        VBox root = new VBox(20,
                predictionLabel,
                tempChart,
                sensorTable,
                deviceHeader, deviceTable, toggleButton
        );
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 900, 900);

        // LIEN AVEC LE FICHIER CSS
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dark-theme.css")).toExternalForm());

        primaryStage.setTitle("SmartHome");
        primaryStage.setScene(scene);
        primaryStage.show();

        // --- TEMPS REEL ---
        fetchSensorData(sensorTable);
        fetchDeviceData(deviceTable);
        fetchPredictionData(predictionLabel);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            fetchSensorData(sensorTable);
            fetchDeviceData(deviceTable);
            fetchPredictionData(predictionLabel);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setupSensorTable(TableView<SensorData> table) {
        TableColumn<SensorData, String> nameCol = new TableColumn<>("Capteur");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        nameCol.setPrefWidth(150);

        TableColumn<SensorData, Number> valueCol = new TableColumn<>("Temp. (°C)");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("sensorValue"));
        valueCol.setPrefWidth(100);

        TableColumn<SensorData, String> timeCol = new TableColumn<>("Date / Heure");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("measureTime"));
        timeCol.setPrefWidth(150);

        table.getColumns().add(nameCol);
        table.getColumns().add(valueCol);
        table.getColumns().add(timeCol);
        table.setPrefHeight(180);
    }

    private void setupDeviceTable(TableView<Device> table) {
        TableColumn<Device, String> nameCol = new TableColumn<>("Appareil");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Device, Number> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);

        TableColumn<Device, String> stateCol = new TableColumn<>("Allumé ?");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        stateCol.setPrefWidth(100);

        table.getColumns().add(nameCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(stateCol);
        table.setPrefHeight(150);
    }

    // --- MÉTHODES DE RÉCUPÉRATION DES DONNÉES ---

    private void fetchSensorData(TableView<SensorData> table) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/sensor-data")).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<SensorData>>(){}.getType();
                    List<SensorData> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> {
                        table.getItems().setAll(list);
                        updateChart(list); // Met à jour le graphique
                    });
                });
    }

    private void updateChart(List<SensorData> list) {
        historySeries.getData().clear();

        // On n'affiche que les 30 derniers points pour que le graphique soit lisible
        int start = Math.max(0, list.size() - 30);
        for (int i = start; i < list.size(); i++) {
            SensorData data = list.get(i);
            // L'axe X est juste un index, l'axe Y est la température
            historySeries.getData().add(new XYChart.Data<>(i - start, data.getSensorValue()));
        }
    }

    private void fetchDeviceData(TableView<Device> table) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/devices")).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<Device>>(){}.getType();
                    List<Device> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> table.getItems().setAll(list));
                });
    }

    private void fetchPredictionData(Label predictionLabel) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/predictions")).GET().build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<Prediction>>(){}.getType();
                    List<Prediction> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> {
                        if (!list.isEmpty()) {
                            Prediction latest = list.getFirst();
                            predictionLabel.setText("Prédiction IA " + latest.getTargetTime() + " : " + latest.getPredictedValue() + "°C");

                            // Ajoute le point de prédiction sur le graphique (à la fin de la ligne)
                            predictionSeries.getData().clear();
                            if (!historySeries.getData().isEmpty()) {
                                int lastX = historySeries.getData().size();
                                predictionSeries.getData().add(new XYChart.Data<>(lastX, latest.getPredictedValue()));
                            }
                        } else {
                            predictionLabel.setText("Prédiction IA : En attente du script Python...");
                        }
                    });
                });
    }

    private void toggleDeviceState(TableView<Device> table) {
        Device selectedDevice = table.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) return;

        selectedDevice.setState(!selectedDevice.isState());
        String jsonBody = gson.toJson(selectedDevice);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/devices/" + selectedDevice.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenRun(() -> Platform.runLater(() -> fetchDeviceData(table)));
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/