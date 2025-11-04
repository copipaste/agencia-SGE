package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.CreateAgenteInput;
import com.agencia.agencia_backend.dto.UpdateAgenteInput;
import com.agencia.agencia_backend.model.Agente;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.repository.AgenteRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AgenteService {

    @Autowired
    private AgenteRepository agenteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los agentes
     */
    public List<Agente> getAllAgentes() {
        return agenteRepository.findAll();
    }

    /**
     * Obtener agente por ID
     */
    public Optional<Agente> getAgenteById(String id) {
        return agenteRepository.findById(id);
    }

    /**
     * Obtener agente por usuario ID
     */
    public Optional<Agente> getAgenteByUsuarioId(String usuarioId) {
        return agenteRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Buscar agentes por nombre o apellido
     */
    public List<Agente> searchAgentes(String searchTerm) {
        List<Agente> agentes = agenteRepository.findAll();
        
        return agentes.stream()
            .filter(agente -> {
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(agente.getUsuarioId());
                if (usuarioOpt.isPresent()) {
                    Usuario usuario = usuarioOpt.get();
                    String fullName = (usuario.getNombre() + " " + usuario.getApellido()).toLowerCase();
                    return fullName.contains(searchTerm.toLowerCase());
                }
                return false;
            })
            .toList();
    }

    /**
     * Crear nuevo agente (crea también el usuario)
     */
    public Agente createAgente(CreateAgenteInput input) {
        // Verificar que el email no esté registrado
        if (usuarioRepository.existsByEmail(input.getEmail())) {
            throw new RuntimeException("Error: El email ya está registrado!");
        }

        // Crear el usuario primero
        Usuario usuario = new Usuario();
        usuario.setEmail(input.getEmail());
        usuario.setPassword(passwordEncoder.encode(input.getPassword()));
        usuario.setNombre(input.getNombre());
        usuario.setApellido(input.getApellido());
        usuario.setTelefono(input.getTelefono());
        usuario.setSexo(input.getSexo());
        usuario.setIsAgente(true);
        usuario.setIsAdmin(false);
        usuario.setIsCliente(false);
        usuario.setIsActive(true);
        
        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Crear el perfil de agente
        Agente agente = new Agente();
        agente.setUsuarioId(savedUsuario.getId());
        agente.setPuesto(input.getPuesto());
        
        // Convertir String a LocalDate si está presente
        if (input.getFechaContratacion() != null && !input.getFechaContratacion().isEmpty()) {
            agente.setFechaContratacion(LocalDate.parse(input.getFechaContratacion()));
        } else {
            // Si no se proporciona fecha, usar la fecha actual
            agente.setFechaContratacion(LocalDate.now());
        }
        
        return agenteRepository.save(agente);
    }

    /**
     * Actualizar agente existente
     */
    public Agente updateAgente(String id, UpdateAgenteInput input) {
        Agente agente = agenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agente no encontrado"));

        if (input.getPuesto() != null) {
            agente.setPuesto(input.getPuesto());
        }

        if (input.getFechaContratacion() != null && !input.getFechaContratacion().isEmpty()) {
            agente.setFechaContratacion(LocalDate.parse(input.getFechaContratacion()));
        }

        return agenteRepository.save(agente);
    }

    /**
     * Eliminar agente (hard delete)
     */
    public boolean deleteAgente(String id) {
        Agente agente = agenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agente no encontrado"));
        
        agenteRepository.delete(agente);
        return true;
    }

    /**
     * Activar/Desactivar agente
     */
    public Agente toggleAgenteStatus(String id) {
        Agente agente = agenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Agente no encontrado"));

        Usuario usuario = usuarioRepository.findById(agente.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setIsActive(!usuario.getIsActive());
        usuarioRepository.save(usuario);

        return agente;
    }
}
