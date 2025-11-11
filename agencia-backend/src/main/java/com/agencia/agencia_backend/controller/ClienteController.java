package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.rest.ActualizarPerfilRequest;
import com.agencia.agencia_backend.dto.rest.ApiResponse;
import com.agencia.agencia_backend.dto.rest.ClientePerfilDTO;
import com.agencia.agencia_backend.service.ClienteRestService;
import com.agencia.agencia_backend.service.GraphQLAuthService;
import com.agencia.agencia_backend.dto.UsuarioDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClienteController {

    @Autowired
    private ClienteRestService clienteService;

    @Autowired
    private GraphQLAuthService authService;

    /**
     * GET /api/clientes/me
     * Obtiene el perfil completo del cliente autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ClientePerfilDTO>> obtenerMiPerfil() {
        try {
            // Obtener usuario autenticado
            UsuarioDTO usuario = authService.getCurrentUser();

            // Verificar que es cliente
            if (!usuario.getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado. Solo clientes pueden acceder a su perfil.", null));
            }

            // Obtener perfil completo
            ClientePerfilDTO perfil = clienteService.obtenerPerfilCompleto(usuario.getId());

            if (perfil == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Perfil de cliente no encontrado", null));
            }

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Perfil obtenido exitosamente", perfil)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al obtener perfil: " + e.getMessage(), null));
        }
    }

    /**
     * PUT /api/clientes/me
     * Actualiza el perfil del cliente autenticado
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ClientePerfilDTO>> actualizarMiPerfil(
            @Valid @RequestBody ActualizarPerfilRequest request) {
        try {
            // Obtener usuario autenticado
            UsuarioDTO usuario = authService.getCurrentUser();

            // Verificar que es cliente
            if (!usuario.getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado. Solo clientes pueden actualizar su perfil.", null));
            }

            // Actualizar perfil
            ClientePerfilDTO perfilActualizado = clienteService.actualizarPerfil(usuario.getId(), request);

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Perfil actualizado exitosamente", perfilActualizado)
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al actualizar perfil: " + e.getMessage(), null));
        }
    }
}

