package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends MongoRepository<Cliente, String> {
    
    Optional<Cliente> findByUsuarioId(String usuarioId);
    
    Optional<Cliente> findByNumeroPasaporte(String numeroPasaporte);
}
