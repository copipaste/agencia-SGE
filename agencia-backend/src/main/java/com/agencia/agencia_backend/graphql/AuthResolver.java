package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.AuthPayload;
import com.agencia.agencia_backend.dto.LoginRequest;
import com.agencia.agencia_backend.dto.RegisterRequest;
import com.agencia.agencia_backend.dto.UsuarioDTO;
import com.agencia.agencia_backend.service.GraphQLAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthResolver {
    
    private final GraphQLAuthService graphQLAuthService;
    
    /**
     * Query: Obtener información del usuario actual autenticado
     * Ejemplo:
     * query {
     *   me {
     *     id
     *     email
     *     nombre
     *     apellido
     *     isAdmin
     *     isAgente
     *     isCliente
     *   }
     * }
     */
    @QueryMapping
    public UsuarioDTO me() {
        return graphQLAuthService.getCurrentUser();
    }
    
    /**
     * Query: Endpoint de prueba
     * Ejemplo:
     * query {
     *   hello
     * }
     */
    @QueryMapping
    public String hello() {
        return "¡Hola desde GraphQL! Backend de Agencia de Viajes";
    }
    
    /**
     * Mutation: Registrar nuevo usuario
     * Ejemplo:
     * mutation {
     *   register(input: {
     *     email: "usuario@example.com"
     *     password: "password123"
     *     nombre: "Juan"
     *     apellido: "Pérez"
     *     telefono: "72345678"
     *     sexo: "M"
     *   }) {
     *     token
     *     type
     *     usuario {
     *       id
     *       email
     *       nombre
     *       apellido
     *       isCliente
     *     }
     *   }
     * }
     */
    @MutationMapping
    public AuthPayload register(@Argument RegisterRequest input) {
        return graphQLAuthService.register(input);
    }
    
    /**
     * Mutation: Iniciar sesión
     * Ejemplo:
     * mutation {
     *   login(input: {
     *     email: "usuario@example.com"
     *     password: "password123"
     *   }) {
     *     token
     *     type
     *     usuario {
     *       id
     *       email
     *       nombre
     *       isAdmin
     *       isAgente
     *       isCliente
     *     }
     *   }
     * }
     */
    @MutationMapping
    public AuthPayload login(@Argument LoginRequest input) {
        return graphQLAuthService.login(input);
    }
}
