package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePerfilDTO {
    // Datos del Usuario
    private String usuarioId;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private String sexo;

    // Datos del Cliente
    private String clienteId;
    private String direccion;
    private String fechaNacimiento; // ISO String
    private String numeroPasaporte;
}

