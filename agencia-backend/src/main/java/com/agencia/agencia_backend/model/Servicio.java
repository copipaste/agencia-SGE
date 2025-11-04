package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "servicios")
public class Servicio {
    
    @Id
    private String id;
    
    private String proveedorId; // Referencia a Proveedor
    
    @NotBlank(message = "El tipo de servicio es obligatorio")
    private String tipoServicio; // Vuelo, Hotel, Tour, etc.
    
    @NotBlank(message = "El nombre del servicio es obligatorio")
    private String nombreServicio;
    
    private String descripcion;
    
    private String destinoCiudad;
    
    private String destinoPais;
    
    @Positive(message = "El precio de costo debe ser positivo")
    private Double precioCosto;
    
    @Positive(message = "El precio de venta debe ser positivo")
    private Double precioVenta;
    
    private Boolean isAvailable;
}
