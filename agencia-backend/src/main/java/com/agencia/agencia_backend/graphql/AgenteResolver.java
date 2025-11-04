package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.CreateAgenteInput;
import com.agencia.agencia_backend.dto.UpdateAgenteInput;
import com.agencia.agencia_backend.model.Agente;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.service.AgenteService;
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
public class AgenteResolver {

    @Autowired
    private AgenteService agenteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Query: Obtener todos los agentes
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Agente> getAllAgentes() {
        return agenteService.getAllAgentes();
    }

    /**
     * Query: Obtener agente por ID
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Agente getAgenteById(@Argument String id) {
        return agenteService.getAgenteById(id).orElse(null);
    }

    /**
     * Query: Obtener agente por usuario ID
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Agente getAgenteByUsuarioId(@Argument String usuarioId) {
        return agenteService.getAgenteByUsuarioId(usuarioId).orElse(null);
    }

    /**
     * Query: Buscar agentes por nombre o apellido
     */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Agente> searchAgentes(@Argument String searchTerm) {
        return agenteService.searchAgentes(searchTerm);
    }

    /**
     * Mutation: Crear nuevo agente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Agente createAgente(@Argument CreateAgenteInput input) {
        return agenteService.createAgente(input);
    }

    /**
     * Mutation: Actualizar agente existente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENTE')")
    public Agente updateAgente(@Argument String id, @Argument UpdateAgenteInput input) {
        return agenteService.updateAgente(id, input);
    }

    /**
     * Mutation: Eliminar agente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteAgente(@Argument String id) {
        return agenteService.deleteAgente(id);
    }

    /**
     * Mutation: Activar/Desactivar agente
     */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Agente toggleAgenteStatus(@Argument String id) {
        return agenteService.toggleAgenteStatus(id);
    }

    /**
     * Field Resolver: Obtener el usuario del agente
     */
    @SchemaMapping(typeName = "Agente", field = "usuario")
    public Usuario usuario(Agente agente) {
        return usuarioRepository.findById(agente.getUsuarioId()).orElse(null);
    }
}
