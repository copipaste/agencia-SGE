package com.agencia.agencia_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateVentaInput {
    
    @NotBlank(message = "El cliente es requerido")
    private String clienteId;
    
    @NotBlank(message = "El agente es requerido")
    private String agenteId;
    
    @NotBlank(message = "El estado de venta es requerido")
    private String estadoVenta; // Pendiente, Confirmada, Cancelada
    
    @NotBlank(message = "El método de pago es requerido")
    private String metodoPago; // Efectivo, Tarjeta, Transferencia
    
    @NotNull(message = "Los detalles de venta son requeridos")
    private List<DetalleVentaItemInput> detalles;

    // Constructors
    public CreateVentaInput() {
    }

    public CreateVentaInput(String clienteId, String agenteId, String estadoVenta, 
                           String metodoPago, List<DetalleVentaItemInput> detalles) {
        this.clienteId = clienteId;
        this.agenteId = agenteId;
        this.estadoVenta = estadoVenta;
        this.metodoPago = metodoPago;
        this.detalles = detalles;
    }

    // Getters and Setters
    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getAgenteId() {
        return agenteId;
    }

    public void setAgenteId(String agenteId) {
        this.agenteId = agenteId;
    }

    public String getEstadoVenta() {
        return estadoVenta;
    }

    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<DetalleVentaItemInput> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaItemInput> detalles) {
        this.detalles = detalles;
    }
}
