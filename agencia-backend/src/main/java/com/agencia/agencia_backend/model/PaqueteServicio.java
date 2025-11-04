package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "paqueteServicios")
public class PaqueteServicio {
    
    @Id
    private String id;
    
    private String paqueteId; // Referencia a PaqueteTuristico
    
    private String servicioId; // Referencia a Servicio
}
