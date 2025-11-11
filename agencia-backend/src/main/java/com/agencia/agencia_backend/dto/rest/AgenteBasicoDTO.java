package com.agencia.agencia_backend.dto.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgenteBasicoDTO {
    private String id;
    private String usuarioId;
    private String nombre; // Del Usuario
    private String apellido; // Del Usuario
    private String email; // Del Usuario
    private String telefono; // Del Usuario
}

