package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictRequestDTO {
    // IDs
    private String ventaId;
    private String clienteId;

    // Features de venta (7)
    private Double montoTotal;
    private Integer esTemporadaAlta; // 0 o 1
    private Integer diaSemanaReserva; // 0-6
    private Integer metodoPagoTarjeta; // 0 o 1
    private Integer tienePaquete; // 0 o 1
    private Integer duracionDias;
    private Integer destinoCategoria; // 0=playa, 1=ciudad, 2=aventura

    // Features de cliente (4)
    private Integer totalComprasPrevias;
    private Integer totalCancelacionesPrevias;
    private Double tasaCancelacionHistorica;
    private Double montoPromedioCompras;
}

