package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.bi.*;
import com.agencia.agencia_backend.service.BiServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para exponer datos del servicio de Business Intelligence
 * al frontend. Actúa como proxy entre el frontend y el microservicio BI.
 * 
 * NOTA: Los endpoints BI NO requieren autenticación JWT (permitAll en SecurityConfig)
 * porque el usuario ya inició sesión en el frontend.
 */
@RestController
@RequestMapping("/api/bi")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${CORS_ALLOWED_ORIGINS:http://localhost:4200}")
public class BiController {
    
    private final BiServiceClient biServiceClient;
    
    /**
     * Health check del servicio BI
     * GET /api/bi/health
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> checkHealth() {
        try {
            log.info("Verificando salud del servicio BI");
            HealthResponse health = biServiceClient.checkHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Error al verificar salud del servicio BI", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new HealthResponse("error"));
        }
    }
    
    /**
     * Obtiene el estado de sincronización
     * GET /api/bi/sync/status
     */
    @GetMapping("/sync/status")
    public ResponseEntity<SyncStatusResponse> getSyncStatus() {
        try {
            log.info("Consultando estado de sincronización del servicio BI");
            SyncStatusResponse status = biServiceClient.getSyncStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error al obtener estado de sincronización", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SyncStatusResponse(false, false, "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Reinicia la sincronización (solo para administradores)
     * POST /api/bi/sync/restart
     */
    @PostMapping("/sync/restart")
    public ResponseEntity<SyncRestartResponse> restartSync() {
        try {
            log.info("Reiniciando sincronización del servicio BI");
            SyncRestartResponse response = biServiceClient.restartSync();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al reiniciar sincronización", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SyncRestartResponse("error", "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Fuerza una sincronización inmediata después de actualizar ventas
     * POST /api/bi/sync/force
     * Más rápido que restart, útil para reflejar cambios en tiempo real
     */
    @PostMapping("/sync/force")
    public ResponseEntity<SyncRestartResponse> forceSync() {
        try {
            log.info("Forzando sincronización inmediata del servicio BI");
            SyncRestartResponse response = biServiceClient.forceSync();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al forzar sincronización", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SyncRestartResponse("error", "Error: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene el resumen del dashboard con todos los KPIs
     * GET /api/bi/dashboard/resumen
     * @param fechaInicio Fecha de inicio (opcional, formato YYYY-MM-DD)
     * @param fechaFin Fecha de fin (opcional, formato YYYY-MM-DD)
     */
    @GetMapping("/dashboard/resumen")
    public ResponseEntity<DashboardResumenResponse> getDashboardResumen(
            @RequestParam(required = false, name = "fecha_inicio") String fechaInicio,
            @RequestParam(required = false, name = "fecha_fin") String fechaFin) {
        try {
            log.info("Obteniendo resumen del dashboard BI (fechaInicio={}, fechaFin={})", fechaInicio, fechaFin);
            DashboardResumenResponse resumen = biServiceClient.getDashboardResumen(fechaInicio, fechaFin);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            log.error("Error al obtener resumen del dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene el KPI de margen bruto
     * GET /api/bi/kpi/margen-bruto
     */
    // @GetMapping("/kpi/margen-bruto")
    // public ResponseEntity<KpiResponse> getMargenBruto() {
    //     try {
    //         log.info("Obteniendo KPI: margen bruto");
    //         KpiResponse kpi = biServiceClient.getMargenBruto();
    //         return ResponseEntity.ok(kpi);
    //     } catch (Exception e) {
    //         log.error("Error al obtener KPI margen bruto", e);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }
    
    // /**
    //  * Obtiene el KPI de tasa de conversión
    //  * GET /api/bi/kpi/tasa-conversion
    //  */
    // @GetMapping("/kpi/tasa-conversion")
    // public ResponseEntity<KpiResponse> getTasaConversion() {
    //     try {
    //         log.info("Obteniendo KPI: tasa de conversión");
    //         KpiResponse kpi = biServiceClient.getTasaConversion();
    //         return ResponseEntity.ok(kpi);
    //     } catch (Exception e) {
    //         log.error("Error al obtener KPI tasa de conversión", e);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }
    
    // /**
    //  * Obtiene el KPI de tasa de cancelación
    //  * GET /api/bi/kpi/tasa-cancelacion
    //  */
    // @GetMapping("/kpi/tasa-cancelacion")
    // public ResponseEntity<KpiResponse> getTasaCancelacion() {
    //     try {
    //         log.info("Obteniendo KPI: tasa de cancelación");
    //         KpiResponse kpi = biServiceClient.getTasaCancelacion();
    //         return ResponseEntity.ok(kpi);
    //     } catch (Exception e) {
    //         log.error("Error al obtener KPI tasa de cancelación", e);
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }
    
    /**
     * Exporta ventas a CSV
     * GET /api/bi/export/ventas.csv
     * @param fechaInicio Fecha de inicio (opcional, formato YYYY-MM-DD)
     * @param fechaFin Fecha de fin (opcional, formato YYYY-MM-DD)
     * @return Redirección a la URL del CSV en el servicio BI
     */
    @GetMapping("/export/ventas.csv")
    public ResponseEntity<Void> exportarVentasCSV(
            @RequestParam(required = false, name = "fecha_inicio") String fechaInicio,
            @RequestParam(required = false, name = "fecha_fin") String fechaFin) {
        try {
            log.info("Exportando ventas a CSV (fechaInicio={}, fechaFin={})", fechaInicio, fechaFin);
            String csvUrl = biServiceClient.getExportVentasUrl(fechaInicio, fechaFin);
            
            // Redirigir al CSV del servicio BI
            return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(csvUrl))
                .build();
        } catch (Exception e) {
            log.error("Error al exportar ventas a CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

