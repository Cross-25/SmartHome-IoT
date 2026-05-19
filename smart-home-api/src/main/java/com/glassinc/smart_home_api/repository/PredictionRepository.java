package com.glassinc.smart_home_api.repository;

import com.glassinc.smart_home_api.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

}
