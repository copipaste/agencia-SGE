package com.agencia.agencia_backend.dto;

public class UpdateAgenteInput {
    
    private String puesto;
    private String fechaContratacion; // formato: YYYY-MM-DD

    // Getters and Setters
    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(String fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }
}
