package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.Agente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgenteRepository extends MongoRepository<Agente, String> {
    
    Optional<Agente> findByUsuarioId(String usuarioId);
}
