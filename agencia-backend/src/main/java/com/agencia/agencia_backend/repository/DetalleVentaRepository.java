package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.DetalleVenta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends MongoRepository<DetalleVenta, String> {
    
    List<DetalleVenta> findByVentaId(String ventaId);
    
    void deleteByVentaId(String ventaId);
}
