package com.glassinc.smart_home.desktop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class SmartHomeController implements Initializable {

    // ── Constantes ──────────────────────────────────────────────────────────
    private static final String API_BASE_URL = "http://localhost:8080/api";

    // ── Services ─────────────────────────────────────────────────────────────
    private final Gson       gson   = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();

    // ── Séries du graphique ──────────────────────────────────────────────────
    private XYChart.Series<Number, Number> historySeries;
    private XYChart.Series<Number, Number> predictionSeries;

    // ── Injections FXML ──────────────────────────────────────────────────────

    // En-tête
    @FXML private Label predictionLabel;

    // Graphique
    @FXML private LineChart<Number, Number> tempChart;

    // Tableau des capteurs
    @FXML private TableView<SensorData>          sensorTable;
    @FXML private TableColumn<SensorData, String> sensorNameCol;
    @FXML private TableColumn<SensorData, Number> sensorValueCol;
    @FXML private TableColumn<SensorData, String> sensorTimeCol;
    @FXML private Label                           sensorCountLabel;

    // Tableau des appareils
    @FXML private TableView<Device>          deviceTable;
    @FXML private TableColumn<Device, String> deviceNameCol;
    @FXML private TableColumn<Device, String> deviceTypeCol;
    @FXML private TableColumn<Device, Boolean> deviceStateCol;

    // Barre de statut
    @FXML private Label statusLabel;
    @FXML private Label statusDot;

    // ── Initialisation ───────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupChart();
        setupSensorTable();
        setupDeviceTable();
        startPolling();
    }

    // ── Configuration du graphique ───────────────────────────────────────────

    private void setupChart() {
        historySeries = new XYChart.Series<>();
        historySeries.setName("Historique capteur");

        predictionSeries = new XYChart.Series<>();
        predictionSeries.setName("Prédiction IA");

        tempChart.getData().add(historySeries);
        tempChart.getData().add(predictionSeries);
    }

    // ── Configuration des colonnes du tableau Capteurs ───────────────────────

    private void setupSensorTable() {
        sensorNameCol.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        sensorValueCol.setCellValueFactory(new PropertyValueFactory<>("sensorValue"));
        sensorTimeCol.setCellValueFactory(new PropertyValueFactory<>("measureTime"));

        // Formater la valeur de température avec une décimale
        sensorValueCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f °C", value.doubleValue()));
                    // Couleur selon la température
                    double temp = value.doubleValue();
                    if (temp > 28) {
                        setStyle("-fx-text-fill: #ff6b6b;");   // chaud → rouge
                    } else if (temp < 18) {
                        setStyle("-fx-text-fill: #74b9ff;");   // froid → bleu
                    } else {
                        setStyle("-fx-text-fill: #00d4aa;");   // normal → vert
                    }
                }
            }
        });
    }

    // ── Configuration des colonnes du tableau Appareils ──────────────────────

    private void setupDeviceTable() {
        deviceNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        deviceTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        deviceStateCol.setCellValueFactory(new PropertyValueFactory<>("state"));

        // Afficher "✓ Allumé" / "✗ Éteint" avec couleur
        deviceStateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean state, boolean empty) {
                super.updateItem(state, empty);
                if (empty || state == null) {
                    setText(null);
                    setStyle("");
                } else {
                    //boolean on = Boolean.parseBoolean(state) || "true".equalsIgnoreCase(state);
                    boolean on = state;
                    setText(on ? "✓ Allumé" : "✗ Éteint");
                    setStyle(on
                            ? "-fx-text-fill: #00d4aa; -fx-font-weight: bold;"
                            : "-fx-text-fill: #7a8499;");
                }
            }
        });
    }

    // ── Polling temps réel ───────────────────────────────────────────────────

    private void startPolling() {
        // Premier chargement immédiat
        refreshAll();

        // Puis toutes les 5 secondes
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> refreshAll())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void refreshAll() {
        fetchSensorData();
        fetchDeviceData();
        fetchPredictionData();
    }

    // ── Récupération des données ─────────────────────────────────────────────

    private void fetchSensorData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/sensor-data"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<SensorData>>() {}.getType();
                    List<SensorData> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> {
                        sensorTable.getItems().setAll(list);
                        sensorCountLabel.setText(list.size() + " relevé(s)");
                        updateChart(list);
                        setStatus(true, "Données reçues");
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> setStatus(false, "Erreur API : " + ex.getMessage()));
                    return null;
                });
    }

    private void fetchDeviceData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/devices"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<Device>>() {}.getType();
                    List<Device> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> deviceTable.getItems().setAll(list));
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> setStatus(false, "Erreur API appareils"));
                    return null;
                });
    }

    private void fetchPredictionData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/predictions"))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    Type listType = new TypeToken<List<Prediction>>() {}.getType();
                    List<Prediction> list = gson.fromJson(json, listType);
                    Platform.runLater(() -> {
                        if (!list.isEmpty()) {
                            Prediction latest = list.getFirst();
                            predictionLabel.setText(
                                    latest.getPredictedValue() + "°C — " + latest.getTargetTime()
                            );
                            // Afficher le point de prédiction sur le graphique
                            predictionSeries.getData().clear();
                            if (!historySeries.getData().isEmpty()) {
                                // 1. On récupère le DERNIER point de l'historique (le présent).
                                XYChart.Data<Number, Number> presentPoint = historySeries.getData().get(historySeries.getData().size() - 1);

                                // 2. On l'ajoute à la série de prédictions pour que la ligne parte du présent
                                predictionSeries.getData().add(new XYChart.Data<>(presentPoint.getXValue(), presentPoint.getYValue()));

                                // 3. On projette la ligne pointillée 10 points dans le futur
                                int futureX = presentPoint.getXValue().intValue() + 10;
                                predictionSeries.getData().add(new XYChart.Data<>(futureX, latest.getPredictedValue()));
                            }
                        } else {
                            predictionLabel.setText("En attente du script Python...");
                        }
                    });
                });
    }

    // ── Mise à jour du graphique ─────────────────────────────────────────────

    private void updateChart(List<SensorData> list) {
        historySeries.getData().clear();
        int start = Math.max(0, list.size() - 30);
        for (int i = start; i < list.size(); i++) {
            SensorData data = list.get(i);
            historySeries.getData().add(
                    new XYChart.Data<>(i - start, data.getSensorValue())
            );
        }
    }

    // ── Action : basculer l'état d'un appareil ───────────────────────────────

    @FXML
    private void toggleDeviceState() {
        Device selected = deviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Sélectionnez un appareil d'abord.");
            return;
        }

        selected.setState(!selected.isState());
        String jsonBody = gson.toJson(selected);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/devices/" + selected.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenRun(() -> Platform.runLater(this::fetchDeviceData))
                .exceptionally(ex -> {
                    Platform.runLater(() -> setStatus(false, "Erreur lors du changement d'état"));
                    return null;
                });
    }

    // ── Barre de statut ──────────────────────────────────────────────────────

    private void setStatus(boolean ok, String message) {
        statusLabel.setText(message);
        statusDot.setStyle(ok
                ? "-fx-text-fill: #00d4aa;"
                : "-fx-text-fill: #ff6b6b;");
    }
}