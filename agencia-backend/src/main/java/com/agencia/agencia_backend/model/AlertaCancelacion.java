package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alertas_cancelacion")
public class AlertaCancelacion {

    @Id
    private String id;

    private String ventaId;
    private String clienteId;
    private String emailCliente;
    private String nombreCliente;
    private String nombrePaquete;
    private String destino;
    private LocalDateTime fechaVenta; // Fecha de inicio del viaje
    private Double montoTotal;
    private Double probabilidadCancelacion;
    private String recomendacion;
    private LocalDateTime fechaPrediccion;
    private Boolean recordatorioEnviado = false;
    private LocalDateTime fechaEnvioRecordatorio;
    private String estadoVenta = "Pendiente";
}

