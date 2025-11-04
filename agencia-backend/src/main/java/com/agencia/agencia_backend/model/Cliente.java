package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clientes")
public class Cliente {
    
    @Id
    private String id;
    
    @NotNull(message = "El usuario es obligatorio")
    private String usuarioId; // referencia a 'usuarios'
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    private LocalDate fechaNacimiento;
    
    @NotBlank(message = "El número de pasaporte es obligatorio")
    private String numeroPasaporte;
}
