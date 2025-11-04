package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.Venta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends MongoRepository<Venta, String> {
    
    List<Venta> findByClienteId(String clienteId);
    
    List<Venta> findByAgenteId(String agenteId);
    
    List<Venta> findByEstadoVenta(String estadoVenta);
}
