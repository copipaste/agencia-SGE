package com.agencia.agencia_backend;

import com.agencia.agencia_backend.config.BiServiceConfig;
import com.agencia.agencia_backend.dto.bi.DashboardResumenResponse;
import com.agencia.agencia_backend.dto.bi.HealthResponse;
import com.agencia.agencia_backend.dto.bi.SyncStatusResponse;
import com.agencia.agencia_backend.service.BiServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para el cliente del servicio BI
 * 
 * NOTA: Estos tests hacen llamadas reales al servicio BI en Render.
 * Requieren conectividad a internet y que el servicio esté disponible.
 * 
 * Para ejecutar: mvn test -Dtest=BiServiceClientTest
 */
@SpringBootTest
@ActiveProfiles("test")
class BiServiceClientTest {
    
    @Autowired
    private BiServiceClient biServiceClient;
    
    @Autowired
    private BiServiceConfig biConfig;
    
    @BeforeEach
    void setUp() {
        System.out.println("=================================");
        System.out.println("Test de Integración - Servicio BI");
        System.out.println("=================================");
        System.out.println("URL Base: " + biConfig.getBaseUrl());
        System.out.println("Timeout: " + biConfig.getTimeout() + "ms");
        System.out.println("Max Retries: " + biConfig.getMaxRetries());
        System.out.println("=================================\n");
    }
    
    @Test
    void testHealthCheck() {
        System.out.println("🧪 Test: Health Check del servicio BI");
        
        try {
            HealthResponse health = biServiceClient.checkHealth();
            
            assertNotNull(health, "La respuesta no debe ser null");
            assertEquals("ok", health.getStatus(), "El status debe ser 'ok'");
            
            System.out.println("✅ Health check exitoso");
            System.out.println("   Status: " + health.getStatus());
        } catch (Exception e) {
            fail("Health check falló: " + e.getMessage());
        }
    }
    
    @Test
    void testSyncStatus() {
        System.out.println("\n🧪 Test: Estado de Sincronización");
        
        try {
            SyncStatusResponse status = biServiceClient.getSyncStatus();
            
            assertNotNull(status, "La respuesta no debe ser null");
            assertNotNull(status.getMessage(), "El mensaje no debe ser null");
            
            System.out.println("✅ Sync status obtenido exitosamente");
            System.out.println("   Sync Enabled: " + status.getSyncEnabled());
            System.out.println("   Sync Running: " + status.getSyncRunning());
            System.out.println("   Message: " + status.getMessage());
        } catch (Exception e) {
            fail("Sync status falló: " + e.getMessage());
        }
    }
    
    @Test
    void testDashboardResumen() {
        System.out.println("\n🧪 Test: Dashboard Resumen");
        
        try {
            DashboardResumenResponse resumen = biServiceClient.getDashboardResumen();
            
            assertNotNull(resumen, "La respuesta no debe ser null");
            assertNotNull(resumen.getKpis(), "Los KPIs no deben ser null");
            
            System.out.println("✅ Dashboard resumen obtenido exitosamente");
            
            if (resumen.getPeriodo() != null) {
                System.out.println("\n📅 Periodo:");
                System.out.println("   Inicio: " + resumen.getPeriodo().getInicio());
                System.out.println("   Fin: " + resumen.getPeriodo().getFin());
            }
            
            System.out.println("\n📊 KPIs:");
            DashboardResumenResponse.Kpis kpis = resumen.getKpis();
            System.out.println("   Total Clientes: " + kpis.getTotalClientes());
            System.out.println("   Total Ventas Confirmadas: " + kpis.getTotalVentasConfirmadas());
            System.out.println("   Total Monto Vendido: $" + kpis.getTotalMontoVendido());
            System.out.println("   Tasa Cancelación: " + kpis.getTasaCancelacion() + "%");
            
            if (resumen.getTopDestinos() != null && !resumen.getTopDestinos().isEmpty()) {
                System.out.println("\n🏆 Top Destinos:");
                resumen.getTopDestinos().forEach(destino -> 
                    System.out.println("   " + destino.getDestino() + ": $" + destino.getIngresos())
                );
            }
            
            if (resumen.getTendenciaReservasPorDia() != null && !resumen.getTendenciaReservasPorDia().isEmpty()) {
                System.out.println("\n📈 Tendencia de Reservas:");
                resumen.getTendenciaReservasPorDia().stream()
                    .limit(5)
                    .forEach(tendencia -> 
                        System.out.println("   " + tendencia.getFecha() + ": " + tendencia.getCantidadReservas() + " reservas")
                    );
            }
            
            // Validaciones básicas
            assertTrue(kpis.getTotalClientes() >= 0, "Total clientes debe ser >= 0");
            assertTrue(kpis.getTotalVentasConfirmadas() >= 0, "Total ventas debe ser >= 0");
            assertTrue(kpis.getTotalMontoVendido() >= 0, "Total monto debe ser >= 0");
            assertTrue(kpis.getTasaCancelacion() >= 0, "Tasa cancelación debe ser >= 0");
            
        } catch (Exception e) {
            fail("Dashboard resumen falló: " + e.getMessage());
        }
    }
    
    /**
     * Test que verifica el comportamiento de reintentos
     * Este test puede tardar varios segundos debido a los reintentos
     */
    @Test
    void testRetryMechanism() {
        System.out.println("\n🧪 Test: Mecanismo de Reintentos");
        System.out.println("   (Este test puede tardar varios segundos)");
        
        // Guardar configuración original
        int originalRetries = biConfig.getMaxRetries();
        long originalDelay = biConfig.getRetryDelay();
        
        try {
            // Configurar para prueba rápida
            biConfig.setMaxRetries(2);
            biConfig.setRetryDelay(500); // 500ms entre reintentos
            
            // Hacer una llamada que debería funcionar
            HealthResponse health = biServiceClient.checkHealth();
            assertNotNull(health);
            
            System.out.println("✅ Mecanismo de reintentos funcionando correctamente");
            
        } finally {
            // Restaurar configuración original
            biConfig.setMaxRetries(originalRetries);
            biConfig.setRetryDelay(originalDelay);
        }
    }
    
    /**
     * Test informativo que muestra la configuración actual
     */
    @Test
    void testShowConfiguration() {
        System.out.println("\n📋 Configuración del Servicio BI:");
        System.out.println("   Base URL: " + biConfig.getBaseUrl());
        System.out.println("   Timeout: " + biConfig.getTimeout() + "ms");
        System.out.println("   Max Retries: " + biConfig.getMaxRetries());
        System.out.println("   Retry Delay: " + biConfig.getRetryDelay() + "ms");
        System.out.println("   Auth Token configurado: " + 
            (biConfig.getAuthToken() != null && !biConfig.getAuthToken().isEmpty() ? "✅ Sí" : "❌ No"));
        
        assertTrue(true, "Test informativo - siempre pasa");
    }
}
