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
@Document(collection = "paquetesTuristicos")
public class PaqueteTuristico {
    
    @Id
    private String id;
    
    @NotBlank(message = "El nombre del paquete es obligatorio")
    private String nombrePaquete;
    
    private String descripcion;
    
    @NotBlank(message = "El destino principal es obligatorio")
    private String destinoPrincipal;
    
    @Positive(message = "La duración debe ser positiva")
    private Integer duracionDias;
    
    @Positive(message = "El precio total debe ser positivo")
    private Double precioTotalVenta;
}
