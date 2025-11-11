package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    private String id;
    private String clienteId;
    private ClienteBasicoDTO cliente; // Objeto completo del cliente
    private String agenteId;
    private AgenteBasicoDTO agente; // Objeto completo del agente
    private String fechaVenta; // ISO String
    private Double montoTotal;
    private String estadoVenta;
    private String metodoPago;
    private String nombrePaquete; // ✅ Nombre del paquete (si existe) para mostrar en listado
}

