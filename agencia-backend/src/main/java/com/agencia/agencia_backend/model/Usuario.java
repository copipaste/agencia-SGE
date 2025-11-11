package com.agencia.agencia_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {
    
    @Id
    private String id;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    
    private String telefono;
    
    @Pattern(regexp = "M|F", message = "El sexo debe ser 'M' o 'F'")
    private String sexo;
    
    // Token FCM para notificaciones push (nullable para no afectar registros existentes)
    private String fcmToken;

    private Boolean isAdmin = false;
    private Boolean isAgente = false;
    private Boolean isCliente = false;
    private Boolean isActive = true;
}
