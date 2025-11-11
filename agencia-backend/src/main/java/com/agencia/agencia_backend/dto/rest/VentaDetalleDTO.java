package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalleDTO {
    private String id;
    private String clienteId;
    private ClienteBasicoDTO cliente; // Objeto completo del cliente
    private String agenteId;
    private AgenteBasicoDTO agente; // Objeto completo del agente
    private String fechaVenta;
    private Double montoTotal;
    private String estadoVenta;
    private String metodoPago;

    // Lista de detalles (cada uno puede tener servicio o paquete)
    private List<DetalleVentaDTO> detalles;
}

