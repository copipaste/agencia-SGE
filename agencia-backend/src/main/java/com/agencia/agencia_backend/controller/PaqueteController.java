package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.rest.ApiResponse;
import com.agencia.agencia_backend.dto.rest.PaqueteTuristicoDTO;
import com.agencia.agencia_backend.dto.rest.PaqueteTuristicoDetalleDTO;
import com.agencia.agencia_backend.service.PaqueteTuristicoRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaqueteController {

    @Autowired
    private PaqueteTuristicoRestService paqueteService;

    /**
     * GET /api/paquetes
     * Lista todos los paquetes turísticos disponibles
     * Filtros opcionales: destino, duracionMin, duracionMax, precioMax
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PaqueteTuristicoDTO>>> listarPaquetes(
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) Integer duracionMin,
            @RequestParam(required = false) Integer duracionMax,
            @RequestParam(required = false) Double precioMax) {
        try {
            List<PaqueteTuristicoDTO> paquetes = paqueteService.listarPaquetes(destino, duracionMin, duracionMax, precioMax);
            return ResponseEntity.ok(
                new ApiResponse<>(true, "Paquetes obtenidos exitosamente", paquetes)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al obtener paquetes: " + e.getMessage(), null));
        }
    }

    /**
     * GET /api/paquetes/{id}
     * Obtiene el detalle completo de un paquete turístico específico
     * Incluye todos los servicios asociados con sus detalles completos
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaqueteTuristicoDetalleDTO>> obtenerPaquete(@PathVariable String id) {
        try {
            PaqueteTuristicoDetalleDTO paquete = paqueteService.obtenerPaqueteDetalladoPorId(id);

            if (paquete == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Paquete no encontrado", null));
            }

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Paquete obtenido exitosamente", paquete)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al obtener paquete: " + e.getMessage(), null));
        }
    }
}

