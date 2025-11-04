package com.agencia.agencia_backend.dto;

public class DetalleVentaItemInput {
    
    private String servicioId; // Puede ser nulo
    private String paqueteId; // Puede ser nulo
    private Integer cantidad;
    private Double precioUnitarioVenta;

    // Constructors
    public DetalleVentaItemInput() {
    }

    public DetalleVentaItemInput(String servicioId, String paqueteId, Integer cantidad, Double precioUnitarioVenta) {
        this.servicioId = servicioId;
        this.paqueteId = paqueteId;
        this.cantidad = cantidad;
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    // Getters and Setters
    public String getServicioId() {
        return servicioId;
    }

    public void setServicioId(String servicioId) {
        this.servicioId = servicioId;
    }

    public String getPaqueteId() {
        return paqueteId;
    }

    public void setPaqueteId(String paqueteId) {
        this.paqueteId = paqueteId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitarioVenta() {
        return precioUnitarioVenta;
    }

    public void setPrecioUnitarioVenta(Double precioUnitarioVenta) {
        this.precioUnitarioVenta = precioUnitarioVenta;
    }
}
