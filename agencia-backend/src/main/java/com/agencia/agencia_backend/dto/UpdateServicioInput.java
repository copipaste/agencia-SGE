package com.agencia.agencia_backend.dto;

import jakarta.validation.constraints.Positive;

public class UpdateServicioInput {
    
    private String proveedorId;
    private String tipoServicio;
    private String nombreServicio;
    private String descripcion;
    private String destinoCiudad;
    private String destinoPais;
    
    @Positive(message = "El precio de costo debe ser positivo")
    private Double precioCosto;
    
    @Positive(message = "El precio de venta debe ser positivo")
    private Double precioVenta;
    
    private Boolean isAvailable;

    // Constructors
    public UpdateServicioInput() {
    }

    public UpdateServicioInput(String proveedorId, String tipoServicio, String nombreServicio, 
                               String descripcion, String destinoCiudad, String destinoPais,
                               Double precioCosto, Double precioVenta, Boolean isAvailable) {
        this.proveedorId = proveedorId;
        this.tipoServicio = tipoServicio;
        this.nombreServicio = nombreServicio;
        this.descripcion = descripcion;
        this.destinoCiudad = destinoCiudad;
        this.destinoPais = destinoPais;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public String getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDestinoCiudad() {
        return destinoCiudad;
    }

    public void setDestinoCiudad(String destinoCiudad) {
        this.destinoCiudad = destinoCiudad;
    }

    public String getDestinoPais() {
        return destinoPais;
    }

    public void setDestinoPais(String destinoPais) {
        this.destinoPais = destinoPais;
    }

    public Double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(Double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
