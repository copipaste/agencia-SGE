package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.Servicio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends MongoRepository<Servicio, String> {
    
    List<Servicio> findByProveedorId(String proveedorId);
    
    List<Servicio> findByTipoServicio(String tipoServicio);
    
    List<Servicio> findByDestinoCiudad(String destinoCiudad);
}
