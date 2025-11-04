package com.agencia.agencia_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateProveedorInput {
    
    @NotBlank(message = "El nombre de la empresa es requerido")
    private String nombreEmpresa;
    
    @NotBlank(message = "El tipo de servicio es requerido")
    private String tipoServicio;
    
    private String contactoNombre;
    
    @Email(message = "El email debe ser válido")
    private String contactoEmail;
    
    private String contactoTelefono;

    // Constructors
    public CreateProveedorInput() {
    }

    public CreateProveedorInput(String nombreEmpresa, String tipoServicio, String contactoNombre, 
                                String contactoEmail, String contactoTelefono) {
        this.nombreEmpresa = nombreEmpresa;
        this.tipoServicio = tipoServicio;
        this.contactoNombre = contactoNombre;
        this.contactoEmail = contactoEmail;
        this.contactoTelefono = contactoTelefono;
    }

    // Getters and Setters
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getContactoNombre() {
        return contactoNombre;
    }

    public void setContactoNombre(String contactoNombre) {
        this.contactoNombre = contactoNombre;
    }

    public String getContactoEmail() {
        return contactoEmail;
    }

    public void setContactoEmail(String contactoEmail) {
        this.contactoEmail = contactoEmail;
    }

    public String getContactoTelefono() {
        return contactoTelefono;
    }

    public void setContactoTelefono(String contactoTelefono) {
        this.contactoTelefono = contactoTelefono;
    }
}
