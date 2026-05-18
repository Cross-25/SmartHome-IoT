package com.glassinc.smart_home_api.controller;

import com.glassinc.smart_home_api.model.Device;
import com.glassinc.smart_home_api.model.SensorData;
import com.glassinc.smart_home_api.repository.DeviceRepository;

import com.glassinc.smart_home_api.repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    @Autowired //Spring injecte automatiquement le repository ici
    private DeviceRepository deviceRepository;

    @GetMapping // READ
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    // CREATE
    @PostMapping // Dit à Spring : "Cette méthode gère les requêtes POST"
    public Device save(@RequestBody Device newDevice) {
        // @RequestBody dit à Spring : "Prends le JSON de la requête et transforme le en objet Device"
        return deviceRepository.save(newDevice); // On sauvegarde
    }

    // UPDATE : Modifier un appareil existant
    @PutMapping("/{id}")
    public Device updateDevice(@PathVariable Long id, @RequestBody Device updatedDevice) {
        // @PathVariable récupère l'ID dans l'URL (ex: /api/devices/1)
        // La méthode save() de Spring fait un UPDATE si l'ID existe déjà !
        updatedDevice.setId(id);
        return deviceRepository.save(updatedDevice);
    }

    // DELETE : Supprimer un appareil
    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable Long id) {
        deviceRepository.deleteById(id);
    }

}
