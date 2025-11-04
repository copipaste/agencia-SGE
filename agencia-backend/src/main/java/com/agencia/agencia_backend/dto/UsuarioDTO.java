package com.agencia.agencia_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    
    private String id;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private String sexo;
    private Boolean isAdmin;
    private Boolean isAgente;
    private Boolean isCliente;
    private Boolean isActive;
}
