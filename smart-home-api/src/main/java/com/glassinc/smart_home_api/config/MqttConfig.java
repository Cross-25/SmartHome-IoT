package com.glassinc.smart_home_api.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

//@Configuration
public class MqttConfig {

    private final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    private final String CLIENT_ID = "springBootSmartHome";
    private final String TOPIC = "maison/salon/temperature";

    //@Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { BROKER_URL });
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    //@Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    //@Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(CLIENT_ID, mqttClientFactory(), TOPIC);
        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    // C'est ici qu'on traite le message directement !
    //@Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) {
                // On récupère le payload (les bytes) et on le convertit en String
                String payload = new String((byte[]) message.getPayload());
                System.out.println(">>> DONNÉE IoT REÇUE VIA MQTT : " + payload);

                // Plus tard, on ajoutera ici la logique pour extraire le JSON
                // et sauvegarder la température dans la base de données !
            }
        };
    }
}