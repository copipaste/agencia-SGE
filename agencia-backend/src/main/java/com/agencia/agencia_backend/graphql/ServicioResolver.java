package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.CreateServicioInput;
import com.agencia.agencia_backend.dto.UpdateServicioInput;
import com.agencia.agencia_backend.model.Proveedor;
import com.agencia.agencia_backend.model.Servicio;
import com.agencia.agencia_backend.repository.ProveedorRepository;
import com.agencia.agencia_backend.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ServicioResolver {

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private ProveedorRepository proveedorRepository;

    /**
     * Query: Obtener todos los servicios
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Servicio> getAllServicios() {
        return servicioService.getAllServicios();
    }

    /**
     * Query: Obtener servicio por ID
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Servicio getServicioById(@Argument String id) {
        return servicioService.getServicioById(id).orElse(null);
    }

    /**
     * Query: Obtener servicios por proveedor
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Servicio> getServiciosByProveedorId(@Argument String proveedorId) {
        return servicioService.getServiciosByProveedorId(proveedorId);
    }

    /**
     * Query: Obtener servicios por tipo
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Servicio> getServiciosByTipo(@Argument String tipoServicio) {
        return servicioService.getServiciosByTipo(tipoServicio);
    }

    /**
     * Query: Obtener servicios por destino
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Servicio> getServiciosByDestino(@Argument String destinoCiudad) {
        return servicioService.getServiciosByDestino(destinoCiudad);
    }

    /**
     * Query: Buscar servicios
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Servicio> searchServicios(@Argument String searchTerm) {
        return servicioService.searchServicios(searchTerm);
    }

    /**
     * Mutation: Crear nuevo servicio
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Servicio createServicio(@Argument CreateServicioInput input) {
        return servicioService.createServicio(input);
    }

    /**
     * Mutation: Actualizar servicio existente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Servicio updateServicio(@Argument String id, @Argument UpdateServicioInput input) {
        return servicioService.updateServicio(id, input);
    }

    /**
     * Mutation: Eliminar servicio
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteServicio(@Argument String id) {
        return servicioService.deleteServicio(id);
    }

    /**
     * Field Resolver: Obtener el proveedor del servicio
     */
    @SchemaMapping(typeName = "Servicio", field = "proveedor")
    public Proveedor proveedor(Servicio servicio) {
        if (servicio.getProveedorId() == null) {
            return null;
        }
        return proveedorRepository.findById(servicio.getProveedorId()).orElse(null);
    }
}
