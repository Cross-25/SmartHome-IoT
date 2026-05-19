# Smart Home — Projet

Projet de démonstration Smart Home contenant deux modules : une API Spring Boot et une application desktop JavaFX.

## Vue d'ensemble
- **API** : backend Spring Boot minimal exposant des endpoints REST pour gérer des `Device` et consulter l'historique `SensorData`. Simule et sauvegarde des mesures (H2 en mémoire) et inclut des exemples d'intégration MQTT.
- **Desktop** : client JavaFX léger prévu pour consommer/afficher les données (prototype).

## Arborescence importante
- **API** : [smart-home-api](smart-home-api)
- **Desktop** : [smart-home-desktop](smart-home-desktop)

## Prérequis
- **Java** : JDK 21
- **Maven** : >= 3.6
- Connexion Internet pour télécharger les dépendances et accéder au broker MQTT public (optionnel)

## Compilation et exécution

API (développement rapide)

1. Lancer depuis la racine ou depuis le dossier `smart-home-api` :

```bash
mvn -f smart-home-api spring-boot:run
```

2. Ou build + exécuter le jar :

```bash
mvn -f smart-home-api clean package
java -jar smart-home-api/target/smart-home-api-0.0.1-SNAPSHOT.jar
```

Desktop (JavaFX)

1. Lancer depuis la racine ou depuis le dossier `smart-home-desktop` :

```bash
mvn -f smart-home-desktop javafx:run
```

Remarque : le plugin `javafx-maven-plugin` est préconfiguré dans `smart-home-desktop/pom.xml` — ajuste la version JavaFX si nécessaire.

## Endpoints principaux (API)

- **Liste des devices** : GET /api/devices
- **Créer device** : POST /api/devices
- **Modifier device** : PUT /api/devices/{id}
- **Supprimer device** : DELETE /api/devices/{id}
- **Historique mesures** : GET /api/sensor-data

Les contrôleurs correspondants se trouvent dans : [smart-home-api/src/main/java/com/glassinc/smart_home_api/controller](smart-home-api/src/main/java/com/glassinc/smart_home_api/controller)

Exemple curl :

```bash
# Récupérer toutes les mesures
curl http://localhost:8080/api/sensor-data

# Créer un device
curl -X POST -H "Content-Type: application/json" -d '{"name":"Mon appareil","type":"switch"}' http://localhost:8080/api/devices
```

## Base de données
- Le projet API utilise une base H2 en mémoire (configuration dans `smart-home-api/src/main/resources/application.properties`). La console H2 est activée : `http://localhost:8080/h2-console`.

## MQTT et simulation de données
- Le projet contient une configuration example pour se connecter à un broker MQTT public (broker.hivemq.com) dans `MqttConfig.java` (commentée). Le service `DataSimulatorService` génère des mesures toutes les 5 secondes et les sauvegarde en base.
- Fichiers utiles :
  - [smart-home-api/src/main/java/com/glassinc/smart_home_api/config/MqttConfig.java](smart-home-api/src/main/java/com/glassinc/smart_home_api/config/MqttConfig.java)
  - [smart-home-api/src/main/java/com/glassinc/smart_home_api/service/DataSimulatorService.java](smart-home-api/src/main/java/com/glassinc/smart_home_api/service/DataSimulatorService.java)

## Tests
- Lancer les tests unitaires du module API :

```bash
mvn -f smart-home-api test
```

## Conseils de développement
- Java 21 est défini comme version cible dans les deux `pom.xml`.
- Pour activer l'écoute MQTT réelle, dé-commenter/adapter les `@Bean` dans `MqttConfig.java` ou implémenter `MqttListenerService`.
- La simulation sauvegarde des `SensorData` via JPA dans H2 ; utile pour développer le front sans capteurs réels.

## Où regarder dans le code
- Application principale API : [smart-home-api/src/main/java/com/glassinc/smart_home_api/SmartHomeApiApplication.java](smart-home-api/src/main/java/com/glassinc/smart_home_api/SmartHomeApiApplication.java)
- Controllers : [smart-home-api/src/main/java/com/glassinc/smart_home_api/controller](smart-home-api/src/main/java/com/glassinc/smart_home_api/controller)
- Modèles : [smart-home-api/src/main/java/com/glassinc/smart_home_api/model](smart-home-api/src/main/java/com/glassinc/smart_home_api/model)
- Repositories : [smart-home-api/src/main/java/com/glassinc/smart_home_api/repository](smart-home-api/src/main/java/com/glassinc/smart_home_api/repository)
- Desktop main : [smart-home-desktop/src/main/java/com/glassinc/smart_home/desktop/SmartHomeApp.java](smart-home-desktop/src/main/java/com/glassinc/smart_home/desktop/SmartHomeApp.java)

## Contribuer
- Fork / clone, crée une branche, ouvre une PR.
- Ajoute des tests pour toute logique métier importante.

## Questions / prochaines étapes
- Souhaitez-vous que j'ajoute des exemples Postman / httpie, des diagrams d'architecture, ou un guide de déploiement Docker ?

---

Fichier généré automatiquement par l'assistant. Pour modifications, éditez `README.md`.
