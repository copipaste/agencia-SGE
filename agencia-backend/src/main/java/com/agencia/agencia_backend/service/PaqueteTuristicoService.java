package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.CreatePaqueteTuristicoInput;
import com.agencia.agencia_backend.dto.UpdatePaqueteTuristicoInput;
import com.agencia.agencia_backend.model.PaqueteServicio;
import com.agencia.agencia_backend.model.PaqueteTuristico;
import com.agencia.agencia_backend.model.Servicio;
import com.agencia.agencia_backend.repository.PaqueteServicioRepository;
import com.agencia.agencia_backend.repository.PaqueteTuristicoRepository;
import com.agencia.agencia_backend.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaqueteTuristicoService {

    @Autowired
    private PaqueteTuristicoRepository paqueteTuristicoRepository;
    
    @Autowired
    private PaqueteServicioRepository paqueteServicioRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;

    // Obtener todos los paquetes turísticos
    public List<PaqueteTuristico> getAllPaquetesTuristicos() {
        return paqueteTuristicoRepository.findAll();
    }

    // Obtener paquete por ID
    public Optional<PaqueteTuristico> getPaqueteTuristicoById(String id) {
        return paqueteTuristicoRepository.findById(id);
    }

    // Obtener paquetes por destino principal
    public List<PaqueteTuristico> getPaquetesTuristicosByDestino(String destino) {
        return paqueteTuristicoRepository.findByDestinoPrincipal(destino);
    }

    // Buscar paquetes por nombre o descripción
    public List<PaqueteTuristico> searchPaquetesTuristicos(String keyword) {
        return paqueteTuristicoRepository.findByNombrePaqueteContainingIgnoreCaseOrDescripcionContainingIgnoreCase(keyword, keyword);
    }

    // Crear nuevo paquete turístico
    public PaqueteTuristico createPaqueteTuristico(CreatePaqueteTuristicoInput input) {
        System.out.println("=== Creando paquete turístico ===");
        System.out.println("Servicios IDs recibidos: " + input.getServiciosIds());
        
        PaqueteTuristico paquete = new PaqueteTuristico();
        paquete.setNombrePaquete(input.getNombrePaquete());
        paquete.setDescripcion(input.getDescripcion());
        paquete.setDestinoPrincipal(input.getDestinoPrincipal());
        paquete.setDuracionDias(input.getDuracionDias());
        paquete.setPrecioTotalVenta(input.getPrecioTotalVenta());
        
        PaqueteTuristico savedPaquete = paqueteTuristicoRepository.save(paquete);
        System.out.println("Paquete guardado con ID: " + savedPaquete.getId());
        
        // Asociar servicios si se proporcionaron
        if (input.getServiciosIds() != null && !input.getServiciosIds().isEmpty()) {
            System.out.println("Asociando " + input.getServiciosIds().size() + " servicios al paquete");
            associateServiciosToPaquete(savedPaquete.getId(), input.getServiciosIds());
        } else {
            System.out.println("No hay servicios para asociar");
        }
        
        return savedPaquete;
    }

    // Actualizar paquete turístico
    public Optional<PaqueteTuristico> updatePaqueteTuristico(String id, UpdatePaqueteTuristicoInput input) {
        return paqueteTuristicoRepository.findById(id).map(paquete -> {
            if (input.getNombrePaquete() != null) {
                paquete.setNombrePaquete(input.getNombrePaquete());
            }
            if (input.getDescripcion() != null) {
                paquete.setDescripcion(input.getDescripcion());
            }
            if (input.getDestinoPrincipal() != null) {
                paquete.setDestinoPrincipal(input.getDestinoPrincipal());
            }
            if (input.getDuracionDias() != null) {
                paquete.setDuracionDias(input.getDuracionDias());
            }
            if (input.getPrecioTotalVenta() != null) {
                paquete.setPrecioTotalVenta(input.getPrecioTotalVenta());
            }
            
            PaqueteTuristico updatedPaquete = paqueteTuristicoRepository.save(paquete);
            
            // Actualizar asociaciones de servicios si se proporcionaron
            if (input.getServiciosIds() != null) {
                // Eliminar asociaciones existentes
                paqueteServicioRepository.deleteByPaqueteId(id);
                // Crear nuevas asociaciones
                if (!input.getServiciosIds().isEmpty()) {
                    associateServiciosToPaquete(id, input.getServiciosIds());
                }
            }
            
            return updatedPaquete;
        });
    }

    // Eliminar paquete turístico
    public boolean deletePaqueteTuristico(String id) {
        if (paqueteTuristicoRepository.existsById(id)) {
            // Eliminar primero las asociaciones con servicios
            paqueteServicioRepository.deleteByPaqueteId(id);
            // Luego eliminar el paquete
            paqueteTuristicoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener servicios de un paquete
    public List<Servicio> getServiciosByPaqueteId(String paqueteId) {
        List<PaqueteServicio> paqueteServicios = paqueteServicioRepository.findByPaqueteId(paqueteId);
        List<String> servicioIds = paqueteServicios.stream()
                .map(PaqueteServicio::getServicioId)
                .toList();
        return servicioRepository.findAllById(servicioIds);
    }

    // Método auxiliar para asociar servicios a un paquete
    private void associateServiciosToPaquete(String paqueteId, List<String> servicioIds) {
        System.out.println("=== Asociando servicios al paquete " + paqueteId + " ===");
        for (String servicioId : servicioIds) {
            System.out.println("Verificando servicio ID: " + servicioId);
            if (servicioRepository.existsById(servicioId)) {
                PaqueteServicio paqueteServicio = new PaqueteServicio();
                paqueteServicio.setPaqueteId(paqueteId);
                paqueteServicio.setServicioId(servicioId);
                PaqueteServicio saved = paqueteServicioRepository.save(paqueteServicio);
                System.out.println("✓ PaqueteServicio guardado con ID: " + saved.getId());
            } else {
                System.out.println("✗ Servicio no existe: " + servicioId);
            }
        }
        System.out.println("=== Finalizado asociación de servicios ===");
    }

    // Agregar un servicio a un paquete existente
    public boolean addServicioToPaquete(String paqueteId, String servicioId) {
        if (paqueteTuristicoRepository.existsById(paqueteId) && servicioRepository.existsById(servicioId)) {
            // Verificar si ya existe la asociación
            List<PaqueteServicio> existing = paqueteServicioRepository.findByPaqueteId(paqueteId);
            boolean alreadyExists = existing.stream()
                    .anyMatch(ps -> ps.getServicioId().equals(servicioId));
            
            if (!alreadyExists) {
                PaqueteServicio paqueteServicio = new PaqueteServicio();
                paqueteServicio.setPaqueteId(paqueteId);
                paqueteServicio.setServicioId(servicioId);
                paqueteServicioRepository.save(paqueteServicio);
                return true;
            }
        }
        return false;
    }

    // Eliminar un servicio de un paquete
    public boolean removeServicioFromPaquete(String paqueteId, String servicioId) {
        List<PaqueteServicio> paqueteServicios = paqueteServicioRepository.findByPaqueteId(paqueteId);
        Optional<PaqueteServicio> toDelete = paqueteServicios.stream()
                .filter(ps -> ps.getServicioId().equals(servicioId))
                .findFirst();
        
        if (toDelete.isPresent()) {
            paqueteServicioRepository.delete(toDelete.get());
            return true;
        }
        return false;
    }
}
