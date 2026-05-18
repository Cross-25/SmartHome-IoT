/*
package com.glassinc.smart_home_api.service;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

@Service
public class MqttListenerService {
    //Cette méthode sera appelée automatiquement à chaque fois qu'un message arrive
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleIncomingMessage(byte[] payload) {
        //On convertit les bytes en texte (UTF-8 est le standard pour le JSON et le texte)
        String message = new String(payload);

        System.out.println(">>> DONNEE IoT RECUE VIA MQTT: " + message);
        // Plus tard, on ajoutera la logique pour sauvegarder ça dans la base de données
    }
}
*/