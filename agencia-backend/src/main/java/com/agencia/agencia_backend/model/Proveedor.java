package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "proveedores")
public class Proveedor {
    
    @Id
    private String id;
    
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombreEmpresa;
    
    @NotBlank(message = "El tipo de servicio es obligatorio")
    private String tipoServicio; // Hotel, Vuelo, Tour, etc.
    
    private String contactoNombre;
    
    @Email(message = "El email debe ser válido")
    private String contactoEmail;
    
    private String contactoTelefono;
}
