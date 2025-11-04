package com.agencia.agencia_backend.dto;

public class UpdateClienteInput {
    
    private String direccion;
    private String fechaNacimiento;
    private String numeroPasaporte;

    // Constructors
    public UpdateClienteInput() {
    }

    public UpdateClienteInput(String direccion, String fechaNacimiento, String numeroPasaporte) {
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.numeroPasaporte = numeroPasaporte;
    }

    // Getters and Setters
    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumeroPasaporte() {
        return numeroPasaporte;
    }

    public void setNumeroPasaporte(String numeroPasaporte) {
        this.numeroPasaporte = numeroPasaporte;
    }
}