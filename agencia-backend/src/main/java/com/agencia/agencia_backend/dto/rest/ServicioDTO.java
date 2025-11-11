package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {
    private String id;
    private String proveedorId;
    private String tipoServicio;
    private String nombreServicio;
    private String descripcion;
    private String destinoCiudad;
    private String destinoPais;
    private Double precioCosto;
    private Double precioVenta;
    private Boolean isAvailable;
}

