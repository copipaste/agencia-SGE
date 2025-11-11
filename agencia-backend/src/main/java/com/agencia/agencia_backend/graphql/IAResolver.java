package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.service.RecordatorioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * Resolver GraphQL para operaciones de Inteligencia Artificial
 * Incluye funciones de predicción de cancelaciones y envío de recordatorios
 */
@Controller
public class IAResolver {

    private static final Logger log = LoggerFactory.getLogger(IAResolver.class);

    @Autowired
    private RecordatorioService recordatorioService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Fuerza el envío de recordatorios de cancelación
     * Solo accesible para usuarios con rol de AGENTE
     *
     * @return Resultado del envío con estadísticas en formato JSON
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public String forzarEnvioRecordatorios() {
        log.info("🔔 GraphQL Mutation: forzarEnvioRecordatorios invocada");
        Map<String, Object> resultado = recordatorioService.forzarEnvioRecordatorios();
        return convertirAJson(resultado);
    }

    /**
     * Obtiene estadísticas de los recordatorios
     * Solo accesible para usuarios con rol de AGENTE
     *
     * @return Estadísticas de recordatorios pendientes y enviados en formato JSON
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public String estadisticasRecordatorios() {
        log.info("📊 GraphQL Query: estadisticasRecordatorios invocada");
        Map<String, Object> estadisticas = recordatorioService.obtenerEstadisticasRecordatorios();
        return convertirAJson(estadisticas);
    }

    /**
     * Convierte un Map a String JSON
     */
    private String convertirAJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Error al convertir Map a JSON: {}", e.getMessage());
            return "{\"error\": \"Error al procesar la respuesta\"}";
        }
    }
}

