package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.CreateServicioInput;
import com.agencia.agencia_backend.dto.UpdateServicioInput;
import com.agencia.agencia_backend.model.Servicio;
import com.agencia.agencia_backend.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    /**
     * Obtener todos los servicios
     */
    public List<Servicio> getAllServicios() {
        return servicioRepository.findAll();
    }

    /**
     * Obtener servicio por ID
     */
    public Optional<Servicio> getServicioById(String id) {
        return servicioRepository.findById(id);
    }

    /**
     * Obtener servicios por proveedor ID
     */
    public List<Servicio> getServiciosByProveedorId(String proveedorId) {
        return servicioRepository.findByProveedorId(proveedorId);
    }

    /**
     * Obtener servicios por tipo
     */
    public List<Servicio> getServiciosByTipo(String tipoServicio) {
        return servicioRepository.findByTipoServicio(tipoServicio);
    }

    /**
     * Obtener servicios por destino
     */
    public List<Servicio> getServiciosByDestino(String destinoCiudad) {
        return servicioRepository.findByDestinoCiudad(destinoCiudad);
    }

    /**
     * Buscar servicios por nombre
     */
    public List<Servicio> searchServicios(String searchTerm) {
        List<Servicio> servicios = servicioRepository.findAll();
        
        return servicios.stream()
            .filter(servicio -> {
                String nombreServicio = servicio.getNombreServicio().toLowerCase();
                String descripcion = servicio.getDescripcion() != null ? 
                    servicio.getDescripcion().toLowerCase() : "";
                String destino = servicio.getDestinoCiudad() != null ? 
                    servicio.getDestinoCiudad().toLowerCase() : "";
                return nombreServicio.contains(searchTerm.toLowerCase()) || 
                       descripcion.contains(searchTerm.toLowerCase()) ||
                       destino.contains(searchTerm.toLowerCase());
            })
            .toList();
    }

    /**
     * Crear nuevo servicio
     */
    public Servicio createServicio(CreateServicioInput input) {
        Servicio servicio = new Servicio();
        servicio.setProveedorId(input.getProveedorId());
        servicio.setTipoServicio(input.getTipoServicio());
        servicio.setNombreServicio(input.getNombreServicio());
        servicio.setDescripcion(input.getDescripcion());
        servicio.setDestinoCiudad(input.getDestinoCiudad());
        servicio.setDestinoPais(input.getDestinoPais());
        servicio.setPrecioCosto(input.getPrecioCosto());
        servicio.setPrecioVenta(input.getPrecioVenta());
        servicio.setIsAvailable(input.getIsAvailable());

        return servicioRepository.save(servicio);
    }

    /**
     * Actualizar servicio existente
     */
    public Servicio updateServicio(String id, UpdateServicioInput input) {
        Servicio servicio = servicioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        // Actualizar solo los campos que no son null
        if (input.getProveedorId() != null) {
            servicio.setProveedorId(input.getProveedorId());
        }
        if (input.getTipoServicio() != null) {
            servicio.setTipoServicio(input.getTipoServicio());
        }
        if (input.getNombreServicio() != null) {
            servicio.setNombreServicio(input.getNombreServicio());
        }
        if (input.getDescripcion() != null) {
            servicio.setDescripcion(input.getDescripcion());
        }
        if (input.getDestinoCiudad() != null) {
            servicio.setDestinoCiudad(input.getDestinoCiudad());
        }
        if (input.getDestinoPais() != null) {
            servicio.setDestinoPais(input.getDestinoPais());
        }
        if (input.getPrecioCosto() != null) {
            servicio.setPrecioCosto(input.getPrecioCosto());
        }
        if (input.getPrecioVenta() != null) {
            servicio.setPrecioVenta(input.getPrecioVenta());
        }
        if (input.getIsAvailable() != null) {
            servicio.setIsAvailable(input.getIsAvailable());
        }

        return servicioRepository.save(servicio);
    }

    /**
     * Eliminar servicio (hard delete)
     */
    public boolean deleteServicio(String id) {
        Servicio servicio = servicioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + id));

        servicioRepository.delete(servicio);
        return true;
    }
}
