package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ventas")
public class Venta {
    
    @Id
    private String id;
    
    private String clienteId; // Referencia a Cliente
    
    private String agenteId; // Referencia a Agente
    
    private LocalDateTime fechaVenta;
    
    private Double montoTotal;
    
    private String estadoVenta; // Pendiente, Confirmada, Cancelada
    
    private String metodoPago; // Efectivo, Tarjeta, Transferencia
}
