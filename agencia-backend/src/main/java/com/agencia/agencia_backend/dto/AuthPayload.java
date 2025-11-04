package com.agencia.agencia_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthPayload {
    
    private String token;
    private String type = "Bearer";
    private UsuarioDTO usuario;
    
    public AuthPayload(String token, UsuarioDTO usuario) {
        this.token = token;
        this.usuario = usuario;
        this.type = "Bearer";
    }
}
