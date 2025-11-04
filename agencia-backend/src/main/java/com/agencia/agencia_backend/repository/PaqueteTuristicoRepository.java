package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.PaqueteTuristico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaqueteTuristicoRepository extends MongoRepository<PaqueteTuristico, String> {
    
    List<PaqueteTuristico> findByDestinoPrincipal(String destinoPrincipal);
    
    List<PaqueteTuristico> findByNombrePaqueteContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombrePaquete, String descripcion);
}
