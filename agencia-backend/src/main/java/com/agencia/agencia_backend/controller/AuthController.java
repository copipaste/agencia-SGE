package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.AuthPayload;
import com.agencia.agencia_backend.dto.rest.LoginRequestRest;
import com.agencia.agencia_backend.dto.rest.RegisterRequestRest;
import com.agencia.agencia_backend.dto.rest.ApiResponse;
import com.agencia.agencia_backend.service.GraphQLAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private GraphQLAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthPayload>> login(@Valid @RequestBody LoginRequestRest loginRequest) {
        try {
            // Reutilizamos el servicio GraphQL existente
            com.agencia.agencia_backend.dto.LoginRequest request = new com.agencia.agencia_backend.dto.LoginRequest();
            request.setEmail(loginRequest.getEmail());
            request.setPassword(loginRequest.getPassword());

            AuthPayload authPayload = authService.login(request);

            // Verificamos que sea un cliente
            if (!authPayload.getUsuario().getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado. Solo clientes pueden usar la app móvil.", null));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Login exitoso", authPayload));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Credenciales inválidas: " + e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthPayload>> register(@Valid @RequestBody RegisterRequestRest registerRequest) {
        try {
            com.agencia.agencia_backend.dto.RegisterRequest request = new com.agencia.agencia_backend.dto.RegisterRequest();
            request.setEmail(registerRequest.getEmail());
            request.setPassword(registerRequest.getPassword());
            request.setNombre(registerRequest.getNombre());
            request.setApellido(registerRequest.getApellido());
            request.setTelefono(registerRequest.getTelefono());
            request.setSexo(registerRequest.getSexo());

            // Campos adicionales de Cliente (opcionales desde Flutter)
            request.setDireccion(registerRequest.getDireccion());
            request.setFechaNacimiento(registerRequest.getFechaNacimiento());
            request.setNumeroPasaporte(registerRequest.getNumeroPasaporte());

            AuthPayload authPayload = authService.register(request);

            // Verificar que el usuario registrado es cliente (debería serlo por defecto)
            if (!authPayload.getUsuario().getIsCliente()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error: Usuario no fue registrado como cliente", null));
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Usuario registrado exitosamente", authPayload));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Error en el registro: " + e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<com.agencia.agencia_backend.dto.UsuarioDTO>> getCurrentUser() {
        try {
            com.agencia.agencia_backend.dto.UsuarioDTO usuario = authService.getCurrentUser();
            return ResponseEntity.ok(new ApiResponse<>(true, "Usuario obtenido exitosamente", usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Usuario no autenticado: " + e.getMessage(), null));
        }
    }
}
