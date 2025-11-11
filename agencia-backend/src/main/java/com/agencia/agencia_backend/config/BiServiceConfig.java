package com.agencia.agencia_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * Configuración para el servicio de Business Intelligence
 */
@Configuration
@ConfigurationProperties(prefix = "bi.service")
@Data
public class BiServiceConfig {
    
    /**
     * URL base del servicio de BI desplegado en Render
     */
    private String baseUrl = "https://sw2-servicio-bi.onrender.com";
    
    /**
     * Token de autenticación para comunicación backend-to-backend
     */
    private String authToken;
    
    /**
     * Timeout en milisegundos para las peticiones HTTP
     */
    private int timeout = 10000; // 10 segundos por defecto
    
    /**
     * Número de reintentos en caso de fallo
     */
    private int maxRetries = 2;
    
    /**
     * Tiempo de espera entre reintentos en milisegundos
     */
    private long retryDelay = 1000; // 1 segundo
}
