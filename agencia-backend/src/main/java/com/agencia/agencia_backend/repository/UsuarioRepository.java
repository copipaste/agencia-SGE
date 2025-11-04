package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    
    Optional<Usuario> findByEmail(String email);
    
    Boolean existsByEmail(String email);
}
