package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {
    private String id;
    private String ventaId;

    // Puede tener servicio O paquete (o ambos en teoría)
    private String servicioId;
    private ServicioDTO servicio; // Objeto completo del servicio

    private String paqueteId;
    private PaqueteDetalladoDTO paquete; // ✅ Objeto completo del paquete con servicios expandidos

    private Integer cantidad;
    private Double precioUnitarioVenta;
    private Double subtotal;
}

