package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO extendido para enviar al microservicio de IA
 * Incluye features ML + datos para recordatorios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictRequestFullDTO {
    // IDs
    private String ventaId;
    private String clienteId;

    // Datos para recordatorios
    private String emailCliente;
    private String nombreCliente;
    private String nombrePaquete;
    private String destino;
    private LocalDateTime fechaVenta; // Fecha de inicio del viaje
    private Double montoTotal;

    // Features ML (11)
    private Integer esTemporadaAlta;
    private Integer diaSemanaReserva;
    private Integer metodoPagoTarjeta;
    private Integer tienePaquete;
    private Integer duracionDias;
    private Integer destinoCategoria;
    private Integer totalComprasPrevias;
    private Integer totalCancelacionesPrevias;
    private Double tasaCancelacionHistorica;
    private Double montoPromedioCompras;
}

