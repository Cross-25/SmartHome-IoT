package com.glassinc.smart_home_api.repository;

import com.glassinc.smart_home_api.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    //C'est VIDE ! Et c'est normal.
    //En héritant de JpaRepository, Spring nous donne gratuitement :
    //save(), findAll(), findById(), delete() etc.
}
