package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "detalleVenta")
public class DetalleVenta {
    
    @Id
    private String id;
    
    private String ventaId; // Referencia a Venta
    
    private String servicioId; // Referencia a Servicio (puede ser nulo)
    
    private String paqueteId; // Referencia a PaqueteTuristico (puede ser nulo)
    
    private Integer cantidad;
    
    private Double precioUnitarioVenta;
    
    private Double subtotal;
}
