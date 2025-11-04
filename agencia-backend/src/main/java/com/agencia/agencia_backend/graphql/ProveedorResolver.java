package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.CreateProveedorInput;
import com.agencia.agencia_backend.dto.UpdateProveedorInput;
import com.agencia.agencia_backend.model.Proveedor;
import com.agencia.agencia_backend.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProveedorResolver {

    @Autowired
    private ProveedorService proveedorService;

    /**
     * Query: Obtener todos los proveedores
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Proveedor> getAllProveedores() {
        return proveedorService.getAllProveedores();
    }

    /**
     * Query: Obtener proveedor por ID
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Proveedor getProveedorById(@Argument String id) {
        return proveedorService.getProveedorById(id).orElse(null);
    }

    /**
     * Query: Obtener proveedores por tipo de servicio
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Proveedor> getProveedoresByTipoServicio(@Argument String tipoServicio) {
        return proveedorService.getProveedoresByTipoServicio(tipoServicio);
    }

    /**
     * Query: Buscar proveedores por nombre de empresa
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Proveedor> searchProveedores(@Argument String searchTerm) {
        return proveedorService.searchProveedores(searchTerm);
    }

    /**
     * Mutation: Crear nuevo proveedor
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Proveedor createProveedor(@Argument CreateProveedorInput input) {
        return proveedorService.createProveedor(input);
    }

    /**
     * Mutation: Actualizar proveedor existente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Proveedor updateProveedor(@Argument String id, @Argument UpdateProveedorInput input) {
        return proveedorService.updateProveedor(id, input);
    }

    /**
     * Mutation: Eliminar proveedor
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteProveedor(@Argument String id) {
        return proveedorService.deleteProveedor(id);
    }
}
