package com.agencia.agencia_backend.dto;

import java.util.List;

public class UpdateVentaInput {
    
    private String estadoVenta; // Pendiente, Confirmada, Cancelada
    private String metodoPago; // Efectivo, Tarjeta, Transferencia
    private List<DetalleVentaItemInput> detalles;

    // Constructors
    public UpdateVentaInput() {
    }

    public UpdateVentaInput(String estadoVenta, String metodoPago, List<DetalleVentaItemInput> detalles) {
        this.estadoVenta = estadoVenta;
        this.metodoPago = metodoPago;
        this.detalles = detalles;
    }

    // Getters and Setters
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
