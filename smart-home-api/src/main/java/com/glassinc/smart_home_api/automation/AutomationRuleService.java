package com.glassinc.smart_home_api.automation;

import com.glassinc.smart_home_api.model.Device;
import com.glassinc.smart_home_api.model.SensorData;
import com.glassinc.smart_home_api.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutomationRuleService {
    private final DeviceRepository deviceRepository;

    public AutomationRuleService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    //Cette méthode sera appellée à chaque nouvelle donnée du capteur
    public void evaluateRules(SensorData newData) {
        // Règle 1 : Si le capteur du salon dépasse 23°C, on allume la Prise Cuisine (ventilateur)
        if ("Capteur Salon".equals(newData.getDeviceName())) {
            // On cherche la prise dans la base de données
            // (Il fat s'assurer que le nom correspnd exactement à ce qu'on a inseré tout à l'heure)
            Optional<Device> priseOpt = deviceRepository.findAll().stream()
                    .filter(d -> "Prise Cuisine".equals(d.getName()))
                    .findFirst();

            if (priseOpt.isPresent()) {
                Device prise = priseOpt.get();
                float temperature = newData.getSensorValue();

                if (temperature > 23.0f && !prise.isState()) {
                    // Il fait chaud ET la prise est éteinte -> On allume !
                    prise.setState(true);
                    deviceRepository.save(prise);
                    System.out.println(">>> \uD83E\uDD16 AUTOMATISATION : Il fait " + temperature + "°C. Allumage du ventilateur !");
                } else if (temperature <= 22.0f && prise.isState()) {
                    // Il fait frais et la prise est allumée -> On éteint !
                    // (On met 22 au lieu de 23 pour éviter que la prise clignote si la temp est de 22.9 puis 23.1)
                    prise.setState(false);
                    deviceRepository.save(prise);
                    System.out.println(">>> \uD83E\uDD16 AUTOMATISATION : Il fait" + temperature + "°C. Extinction du ventilateur !");
                }
            }
        }
    }
}
