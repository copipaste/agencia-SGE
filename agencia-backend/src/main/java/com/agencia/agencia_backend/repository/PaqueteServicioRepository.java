package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.PaqueteServicio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaqueteServicioRepository extends MongoRepository<PaqueteServicio, String> {
    
    List<PaqueteServicio> findByPaqueteId(String paqueteId);
    
    List<PaqueteServicio> findByServicioId(String servicioId);
    
    void deleteByPaqueteId(String paqueteId);
}
