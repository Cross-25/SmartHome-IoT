package com.glassinc.smart_home_api;

import com.glassinc.smart_home_api.model.Device;
import com.glassinc.smart_home_api.repository.DeviceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartHomeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartHomeApiApplication.class, args);
	}

	//@Bean
	//CommandLineRunner initDatabase(DeviceRepository deviceRepository) {
	//	return args -> {
	//	deviceRepository.save(new Device("Lampe Salon", "LUMIERE", true));
	//	deviceRepository.save(new Device("Capteur Chambre", "TEMPERATURE", false));
	//	System.out.println(">>> Base de données initialisée avec 2 appareils !");
	//};
	//	}

}
