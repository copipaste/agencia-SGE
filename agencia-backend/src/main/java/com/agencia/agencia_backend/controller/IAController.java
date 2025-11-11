package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.rest.ApiResponse;
import com.agencia.agencia_backend.dto.rest.PredictResponseDTO;
import com.agencia.agencia_backend.service.IAIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@CrossOrigin(origins = "*", maxAge = 3600)
public class IAController {

    @Autowired
    private IAIntegrationService iaService;

    /**
     * POST /api/ia/cancelacion/predict
     * Predice la probabilidad de cancelación de una reserva
     * Request body: { "ventaId": "..." }
     */
    @PostMapping("/cancelacion/predict")
    public ResponseEntity<ApiResponse<PredictResponseDTO>> predecirCancelacion(
            @RequestBody Map<String, String> request) {
        try {
            String ventaId = request.get("ventaId");

            if (ventaId == null || ventaId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "El campo 'ventaId' es obligatorio", null));
            }

            PredictResponseDTO prediccion = iaService.predecirCancelacion(ventaId);

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Predicción realizada exitosamente", prediccion)
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false,
                    "Error al realizar predicción: " + e.getMessage(), null));
        }
    }

    /**
     * GET /api/ia/recomendaciones
     * Obtiene recomendaciones de paquetes turísticos personalizadas
     * [PLACEHOLDER - A implementar con sistema de recomendación]
     */
    @GetMapping("/recomendaciones")
    public ResponseEntity<ApiResponse<Object>> obtenerRecomendaciones() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(new ApiResponse<>(false,
                "Endpoint de recomendaciones no implementado aún. Pendiente de integración con sistema de recomendación basado en IA.",
                null));
    }

    /**
     * POST /api/ia/asistente-paquetes
     * Asistente conversacional para búsqueda de paquetes turísticos
     * [PLACEHOLDER - A implementar con LLM/ChatGPT]
     */
    @PostMapping("/asistente-paquetes")
    public ResponseEntity<ApiResponse<Object>> asistentePaquetes(@RequestBody Object request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
            .body(new ApiResponse<>(false,
                "Endpoint de asistente conversacional no implementado aún. Pendiente de integración con modelo de lenguaje (LLM).",
                null));
    }
}

