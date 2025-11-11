package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.rest.PaqueteTuristicoDTO;
import com.agencia.agencia_backend.dto.rest.PaqueteTuristicoDetalleDTO;
import com.agencia.agencia_backend.dto.rest.ServicioDTO;
import com.agencia.agencia_backend.model.PaqueteTuristico;
import com.agencia.agencia_backend.model.Servicio;
import com.agencia.agencia_backend.repository.PaqueteTuristicoRepository;
import com.agencia.agencia_backend.repository.PaqueteServicioRepository;
import com.agencia.agencia_backend.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaqueteTuristicoRestService {

    @Autowired
    private PaqueteTuristicoRepository paqueteRepository;

    @Autowired
    private PaqueteServicioRepository paqueteServicioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    /**
     * Lista todos los paquetes turísticos con filtros opcionales
     */
    public List<PaqueteTuristicoDTO> listarPaquetes(String destino, Integer duracionMin,
                                                     Integer duracionMax, Double precioMax) {
        List<PaqueteTuristico> paquetes = paqueteRepository.findAll();

        // Aplicar filtros
        return paquetes.stream()
            .filter(p -> destino == null || p.getDestinoPrincipal().toLowerCase().contains(destino.toLowerCase()))
            .filter(p -> duracionMin == null || p.getDuracionDias() >= duracionMin)
            .filter(p -> duracionMax == null || p.getDuracionDias() <= duracionMax)
            .filter(p -> precioMax == null || p.getPrecioTotalVenta() <= precioMax)
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un paquete turístico por su ID (para listados)
     */
    public PaqueteTuristicoDTO obtenerPaquetePorId(String id) {
        return paqueteRepository.findById(id)
            .map(this::convertirADTO)
            .orElse(null);
    }

    /**
     * Obtiene un paquete turístico con todos los detalles de servicios incluidos
     */
    public PaqueteTuristicoDetalleDTO obtenerPaqueteDetalladoPorId(String id) {
        return paqueteRepository.findById(id)
            .map(this::convertirADTODetallado)
            .orElse(null);
    }

    /**
     * Convierte una entidad PaqueteTuristico a DTO (solo con IDs de servicios)
     */
    private PaqueteTuristicoDTO convertirADTO(PaqueteTuristico paquete) {
        // Obtener IDs de servicios relacionados
        List<String> serviciosIds = paqueteServicioRepository.findAll().stream()
            .filter(ps -> ps.getPaqueteId().equals(paquete.getId()))
            .map(ps -> ps.getServicioId())
            .collect(Collectors.toList());

        return new PaqueteTuristicoDTO(
            paquete.getId(),
            paquete.getNombrePaquete(),
            paquete.getDescripcion(),
            paquete.getDestinoPrincipal(),
            paquete.getDuracionDias(),
            paquete.getPrecioTotalVenta(),
            serviciosIds
        );
    }

    /**
     * Convierte una entidad PaqueteTuristico a DTO detallado con servicios completos
     */
    private PaqueteTuristicoDetalleDTO convertirADTODetallado(PaqueteTuristico paquete) {
        // Obtener IDs de servicios relacionados
        List<String> serviciosIds = paqueteServicioRepository.findAll().stream()
            .filter(ps -> ps.getPaqueteId().equals(paquete.getId()))
            .map(ps -> ps.getServicioId())
            .collect(Collectors.toList());

        // Obtener los servicios completos con todos sus datos
        List<ServicioDTO> servicios = serviciosIds.stream()
            .map(servicioId -> servicioRepository.findById(servicioId).orElse(null))
            .filter(servicio -> servicio != null) // Filtrar servicios que no existan
            .map(this::convertirServicioADTO)
            .collect(Collectors.toList());

        return new PaqueteTuristicoDetalleDTO(
            paquete.getId(),
            paquete.getNombrePaquete(),
            paquete.getDescripcion(),
            paquete.getDestinoPrincipal(),
            paquete.getDuracionDias(),
            paquete.getPrecioTotalVenta(),
            servicios
        );
    }

    /**
     * Convierte una entidad Servicio a ServicioDTO
     */
    private ServicioDTO convertirServicioADTO(Servicio servicio) {
        return new ServicioDTO(
            servicio.getId(),
            servicio.getProveedorId(),
            servicio.getTipoServicio(),
            servicio.getNombreServicio(),
            servicio.getDescripcion(),
            servicio.getDestinoCiudad(),
            servicio.getDestinoPais(),
            servicio.getPrecioCosto(),
            servicio.getPrecioVenta(),
            servicio.getIsAvailable()
        );
    }
}

