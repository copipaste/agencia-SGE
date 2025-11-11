package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.config.BiServiceConfig;
import com.agencia.agencia_backend.dto.bi.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cliente para comunicación con el microservicio de Business Intelligence
 * Maneja la comunicación backend-to-backend con reintentos y timeout
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BiServiceClient {
    
    private final RestTemplate restTemplate;
    private final BiServiceConfig biConfig;
    
    /**
     * Crea los headers HTTP con autenticación Bearer
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Añadir token de autenticación si está configurado
        if (biConfig.getAuthToken() != null && !biConfig.getAuthToken().isEmpty()) {
            headers.setBearerAuth(biConfig.getAuthToken());
        }
        
        return headers;
    }
    
    /**
     * Realiza una petición GET con reintentos
     */
    private <T> T executeGetWithRetry(String endpoint, Class<T> responseType) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < biConfig.getMaxRetries()) {
            try {
                String url = biConfig.getBaseUrl() + endpoint;
                log.debug("Llamando al servicio BI: {} (intento {})", url, attempts + 1);
                
                HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
                ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    responseType
                );
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.debug("Respuesta exitosa del servicio BI: {}", endpoint);
                    return response.getBody();
                }
                
            } catch (RestClientException e) {
                lastException = e;
                log.warn("Error en intento {} para {}: {}", attempts + 1, endpoint, e.getMessage());
                attempts++;
                
                if (attempts < biConfig.getMaxRetries()) {
                    try {
                        Thread.sleep(biConfig.getRetryDelay() * attempts); // Backoff exponencial
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Reintentos interrumpidos", ie);
                    }
                }
            }
        }
        
        log.error("Fallo después de {} intentos para {}", biConfig.getMaxRetries(), endpoint);
        throw new RuntimeException("No se pudo conectar al servicio BI después de " + 
            biConfig.getMaxRetries() + " intentos", lastException);
    }
    
    /**
     * Realiza una petición POST con reintentos
     */
    private <T> T executePostWithRetry(String endpoint, Class<T> responseType) {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < biConfig.getMaxRetries()) {
            try {
                String url = biConfig.getBaseUrl() + endpoint;
                log.debug("POST al servicio BI: {} (intento {})", url, attempts + 1);
                
                HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
                ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
                );
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.debug("POST exitoso al servicio BI: {}", endpoint);
                    return response.getBody();
                }
                
            } catch (RestClientException e) {
                lastException = e;
                log.warn("Error en intento {} para POST {}: {}", attempts + 1, endpoint, e.getMessage());
                attempts++;
                
                if (attempts < biConfig.getMaxRetries()) {
                    try {
                        Thread.sleep(biConfig.getRetryDelay() * attempts);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Reintentos interrumpidos", ie);
                    }
                }
            }
        }
        
        log.error("Fallo POST después de {} intentos para {}", biConfig.getMaxRetries(), endpoint);
        throw new RuntimeException("No se pudo realizar POST al servicio BI después de " + 
            biConfig.getMaxRetries() + " intentos", lastException);
    }
    
    /**
     * Verifica el estado de salud del servicio BI
     */
    public HealthResponse checkHealth() {
        return executeGetWithRetry("/health", HealthResponse.class);
    }
    
    /**
     * Obtiene el estado de la sincronización en tiempo real
     */
    public SyncStatusResponse getSyncStatus() {
        return executeGetWithRetry("/sync/status", SyncStatusResponse.class);
    }
    
    /**
     * Reinicia la sincronización en tiempo real
     */
    public SyncRestartResponse restartSync() {
        return executePostWithRetry("/sync/restart", SyncRestartResponse.class);
    }
    
    /**
     * Fuerza una sincronización inmediata (más rápido que restart)
     * Útil después de actualizar ventas para reflejar cambios en BI
     */
    public SyncRestartResponse forceSync() {
        log.info("Forzando sincronización inmediata del servicio BI");
        return executePostWithRetry("/sync/force", SyncRestartResponse.class);
    }
    
    /**
     * Obtiene el resumen del dashboard con KPIs y métricas
     * @param fechaInicio Fecha de inicio del período (opcional, formato YYYY-MM-DD)
     * @param fechaFin Fecha de fin del período (opcional, formato YYYY-MM-DD)
     */
    public DashboardResumenResponse getDashboardResumen(String fechaInicio, String fechaFin) {
        StringBuilder endpoint = new StringBuilder("/dashboard/resumen");
        
        // Construir parámetros de query
        List<String> params = new ArrayList<>();
        
        // Solo agregar parámetros si no son null, vacíos o "null" (string)
        if (fechaInicio != null && !fechaInicio.isEmpty() && !fechaInicio.equalsIgnoreCase("null")) {
            params.add("fecha_inicio=" + fechaInicio);
            log.debug("Agregando fecha_inicio: {}", fechaInicio);
        }
        if (fechaFin != null && !fechaFin.isEmpty() && !fechaFin.equalsIgnoreCase("null")) {
            params.add("fecha_fin=" + fechaFin);
            log.debug("Agregando fecha_fin: {}", fechaFin);
        }
        
        if (!params.isEmpty()) {
            endpoint.append("?").append(String.join("&", params));
        }
        
        log.info("Llamando a BI Service: {}", endpoint);
        return executeGetWithRetry(endpoint.toString(), DashboardResumenResponse.class);
    }
    
    /**
     * Obtiene el KPI de margen bruto
     */
    public KpiResponse getMargenBruto() {
        return executeGetWithRetry("/kpi/margen-bruto", KpiResponse.class);
    }
    
    /**
     * Obtiene el KPI de tasa de conversión
     */
    public KpiResponse getTasaConversion() {
        return executeGetWithRetry("/kpi/tasa-conversion", KpiResponse.class);
    }
    
    /**
     * Obtiene el KPI de tasa de cancelación
     */
    public KpiResponse getTasaCancelacion() {
        return executeGetWithRetry("/kpi/tasa-cancelacion", KpiResponse.class);
    }
    
    /**
     * Obtiene la URL para exportar ventas a CSV
     * @param fechaInicio Fecha de inicio (opcional)
     * @param fechaFin Fecha de fin (opcional)
     * @return URL completa del CSV
     */
    public String getExportVentasUrl(String fechaInicio, String fechaFin) {
        StringBuilder url = new StringBuilder(biConfig.getBaseUrl() + "/export/ventas.csv");
        
        List<String> params = new ArrayList<>();
        if (fechaInicio != null && !fechaInicio.isEmpty() && !fechaInicio.equalsIgnoreCase("null")) {
            params.add("fecha_inicio=" + fechaInicio);
        }
        if (fechaFin != null && !fechaFin.isEmpty() && !fechaFin.equalsIgnoreCase("null")) {
            params.add("fecha_fin=" + fechaFin);
        }
        
        if (!params.isEmpty()) {
            url.append("?").append(String.join("&", params));
        }
        
        return url.toString();
    }
}
