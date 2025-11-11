package com.agencia.agencia_backend.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("venta_id")
    private String ventaId;

    @JsonProperty("cliente_id")
    private String clienteId;

    // Datos para recordatorios
    @JsonProperty("email_cliente")
    private String emailCliente;

    @JsonProperty("nombre_cliente")
    private String nombreCliente;

    @JsonProperty("nombre_paquete")
    private String nombrePaquete;

    @JsonProperty("destino")
    private String destino;

    @JsonProperty("fecha_venta")
    private LocalDateTime fechaVenta; // Fecha de inicio del viaje

    @JsonProperty("monto_total")
    private Double montoTotal;

    // Features ML (11)
    @JsonProperty("es_temporada_alta")
    private Integer esTemporadaAlta;

    @JsonProperty("dia_semana_reserva")
    private Integer diaSemanaReserva;

    @JsonProperty("metodo_pago_tarjeta")
    private Integer metodoPagoTarjeta;

    @JsonProperty("tiene_paquete")
    private Integer tienePaquete;

    @JsonProperty("duracion_dias")
    private Integer duracionDias;

    @JsonProperty("destino_categoria")
    private Integer destinoCategoria;

    @JsonProperty("total_compras_previas")
    private Integer totalComprasPrevias;

    @JsonProperty("total_cancelaciones_previas")
    private Integer totalCancelacionesPrevias;

    @JsonProperty("tasa_cancelacion_historica")
    private Double tasaCancelacionHistorica;

    @JsonProperty("monto_promedio_compras")
    private Double montoPromedioCompras;
}

