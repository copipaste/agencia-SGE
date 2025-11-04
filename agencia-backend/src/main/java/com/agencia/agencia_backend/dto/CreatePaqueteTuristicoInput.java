package com.agencia.agencia_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class CreatePaqueteTuristicoInput {
    
    @NotBlank(message = "El nombre del paquete es requerido")
    private String nombrePaquete;
    
    private String descripcion;
    
    @NotBlank(message = "El destino principal es requerido")
    private String destinoPrincipal;
    
    @Positive(message = "La duración debe ser positiva")
    private Integer duracionDias;
    
    @Positive(message = "El precio total debe ser positivo")
    private Double precioTotalVenta;
    
    // Lista de IDs de servicios para asociar al paquete
    private List<String> serviciosIds;

    // Constructors
    public CreatePaqueteTuristicoInput() {
    }

    public CreatePaqueteTuristicoInput(String nombrePaquete, String descripcion, String destinoPrincipal,
                                       Integer duracionDias, Double precioTotalVenta, List<String> serviciosIds) {
        this.nombrePaquete = nombrePaquete;
        this.descripcion = descripcion;
        this.destinoPrincipal = destinoPrincipal;
        this.duracionDias = duracionDias;
        this.precioTotalVenta = precioTotalVenta;
        this.serviciosIds = serviciosIds;
    }

    // Getters and Setters
    public String getNombrePaquete() {
        return nombrePaquete;
    }

    public void setNombrePaquete(String nombrePaquete) {
        this.nombrePaquete = nombrePaquete;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDestinoPrincipal() {
        return destinoPrincipal;
    }

    public void setDestinoPrincipal(String destinoPrincipal) {
        this.destinoPrincipal = destinoPrincipal;
    }

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public Double getPrecioTotalVenta() {
        return precioTotalVenta;
    }

    public void setPrecioTotalVenta(Double precioTotalVenta) {
        this.precioTotalVenta = precioTotalVenta;
    }

    public List<String> getServiciosIds() {
        return serviciosIds;
    }

    public void setServiciosIds(List<String> serviciosIds) {
        this.serviciosIds = serviciosIds;
    }
}
