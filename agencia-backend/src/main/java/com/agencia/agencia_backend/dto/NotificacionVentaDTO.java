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
public class NotificacionVentaDTO {
    
    private String ventaId;
    private String fechaVenta;
    private ClienteNotificacionDTO cliente;
    private AgenteNotificacionDTO agente;
    private Double montoTotal;
    private String estadoVenta;
    private String metodoPago;
    private List<DetalleNotificacionDTO> detalles;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteNotificacionDTO {
        private String nombre;
        private String telefono;
        private String email;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgenteNotificacionDTO {
        private String nombre;
        private String codigo;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleNotificacionDTO {
        private String servicio;
        private Integer cantidad;
        private Double precio;
        private Double subtotal;
    }
}
