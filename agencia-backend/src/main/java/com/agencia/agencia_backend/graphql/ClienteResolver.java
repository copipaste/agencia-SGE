package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.CreateClienteInput;
import com.agencia.agencia_backend.dto.UpdateClienteInput;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.service.ClienteService;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class ClienteResolver {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Query: Obtener todos los clientes
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Cliente> getAllClientes() {
        return clienteService.getAllClientes();
    }

    /**
     * Query: Obtener cliente por ID
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE') or hasRole('CLIENTE')")
    public Cliente getClienteById(@Argument String id) {
        return clienteService.getClienteById(id).orElse(null);
    }

    /**
     * Query: Obtener cliente por usuario ID
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE') or hasRole('CLIENTE')")
    public Cliente getClienteByUsuarioId(@Argument String usuarioId) {
        return clienteService.getClienteByUsuarioId(usuarioId).orElse(null);
    }

    /**
     * Query: Buscar clientes por nombre o apellido
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public List<Cliente> searchClientes(@Argument String searchTerm) {
        return clienteService.searchClientes(searchTerm);
    }

    /**
     * Mutation: Crear nuevo cliente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Cliente createCliente(@Argument CreateClienteInput input) {
        return clienteService.createCliente(input);
    }

    /**
     * Mutation: Actualizar cliente existente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE') or hasRole('CLIENTE')")
    public Cliente updateCliente(@Argument String id, @Argument UpdateClienteInput input) {
        return clienteService.updateCliente(id, input);
    }

    /**
     * Mutation: Eliminar cliente (soft delete)
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteCliente(@Argument String id) {
        return clienteService.deleteCliente(id);
    }

    /**
     * Mutation: Activar/Desactivar cliente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Cliente toggleClienteStatus(@Argument String id) {
        return clienteService.toggleClienteStatus(id);
    }

    /**
     * Field Resolver: Obtener el usuario del cliente
     */
    @SchemaMapping(typeName = "Cliente", field = "usuario")
    public Usuario usuario(Cliente cliente) {
        return usuarioRepository.findById(cliente.getUsuarioId()).orElse(null);
    }
}
