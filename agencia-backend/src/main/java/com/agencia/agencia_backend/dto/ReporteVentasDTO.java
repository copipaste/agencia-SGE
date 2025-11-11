package com.agencia.agencia_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasDTO {
    
    private String periodo; // "SEMANAL", "MENSUAL", "ANUAL"
    private String fechaInicio;
    private String fechaFin;
    private Integer totalVentas;
    private Double montoTotal;
    private Double promedioVenta;
    private List<VentaPorEstadoDTO> ventasPorEstado;
    private List<VentaPorMetodoPagoDTO> ventasPorMetodoPago;
    private List<TopAgenteDTO> topAgentes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaPorEstadoDTO {
        private String estado;
        private Integer cantidad;
        private Double monto;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VentaPorMetodoPagoDTO {
        private String metodoPago;
        private Integer cantidad;
        private Double monto;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopAgenteDTO {
        private String agenteId;
        private String agenteNombre;
        private Integer cantidadVentas;
        private Double montoTotal;
    }
}
