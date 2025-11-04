package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.Proveedor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends MongoRepository<Proveedor, String> {
    
    List<Proveedor> findByTipoServicio(String tipoServicio);
}
