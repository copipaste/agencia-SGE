package com.agencia.agencia_backend.dto.bi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO para la respuesta del endpoint /dashboard/resumen del servicio BI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenResponse {
    
    private Periodo periodo;
    private Kpis kpis;
    
    @JsonProperty("top_destinos")
    private List<TopDestino> topDestinos;
    
    @JsonProperty("tendencia_reservas_por_dia")
    private List<TendenciaReserva> tendenciaReservasPorDia;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Periodo {
        private String inicio;
        private String fin;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Kpis {
        @JsonProperty("total_clientes")
        private Integer totalClientes;
        
        @JsonProperty("total_ventas_confirmadas")
        private Integer totalVentasConfirmadas;
        
        @JsonProperty("total_ventas_pendientes")
        private Integer totalVentasPendientes;
        
        @JsonProperty("total_ventas_canceladas")
        private Integer totalVentasCanceladas;
        
        @JsonProperty("total_ventas")
        private Integer totalVentas;
        
        @JsonProperty("total_monto_vendido")
        private Double totalMontoVendido;
        
        @JsonProperty("total_monto_pendiente")
        private Double totalMontoPendiente;
        
        @JsonProperty("tasa_cancelacion")
        private Double tasaCancelacion;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopDestino {
        private String destino;
        private Double ingresos;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TendenciaReserva {
        private String fecha;
        
        @JsonProperty("cantidad_reservas")
        private Integer cantidadReservas;
    }
}
