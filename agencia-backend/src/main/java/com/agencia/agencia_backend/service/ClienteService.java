package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.CreateClienteInput;
import com.agencia.agencia_backend.dto.UpdateClienteInput;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private static final String CLIENTE_NOT_FOUND_MSG = "Cliente no encontrado con ID: ";

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los clientes
     */
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Obtener cliente por ID
     */
    public Optional<Cliente> getClienteById(String id) {
        return clienteRepository.findById(id);
    }

    /**
     * Obtener cliente por usuario ID
     */
    public Optional<Cliente> getClienteByUsuarioId(String usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Buscar clientes por nombre o apellido
     */
    public List<Cliente> searchClientes(String searchTerm) {
        // Buscar en el repositorio por coincidencias en usuario
        List<Cliente> clientes = clienteRepository.findAll();
        
        return clientes.stream()
            .filter(cliente -> {
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(cliente.getUsuarioId());
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
     * Crear nuevo cliente (crea también el usuario)
     */
    public Cliente createCliente(CreateClienteInput input) {
        // Verificar que el email no esté registrado
        if (usuarioRepository.existsByEmail(input.getEmail())) {
            throw new RuntimeException("Error: El email ya está registrado!");
        }

        // Verificar que el número de pasaporte no esté duplicado
        Optional<Cliente> clienteWithSamePassport = clienteRepository.findByNumeroPasaporte(input.getNumeroPasaporte());
        if (clienteWithSamePassport.isPresent()) {
            throw new RuntimeException("Ya existe un cliente con ese número de pasaporte");
        }

        // Crear el usuario primero
        Usuario usuario = new Usuario();
        usuario.setEmail(input.getEmail());
        usuario.setPassword(passwordEncoder.encode(input.getPassword()));
        usuario.setNombre(input.getNombre());
        usuario.setApellido(input.getApellido());
        usuario.setTelefono(input.getTelefono());
        usuario.setSexo(input.getSexo());
        usuario.setIsCliente(true);
        usuario.setIsAdmin(false);
        usuario.setIsAgente(false);
        usuario.setIsActive(true);
        
        Usuario savedUsuario = usuarioRepository.save(usuario);

        // Crear el perfil de cliente
        Cliente cliente = new Cliente();
        cliente.setUsuarioId(savedUsuario.getId());
        cliente.setDireccion(input.getDireccion());
        
        // Convertir String a LocalDate si está presente
        if (input.getFechaNacimiento() != null && !input.getFechaNacimiento().isEmpty()) {
            cliente.setFechaNacimiento(LocalDate.parse(input.getFechaNacimiento()));
        }
        
        cliente.setNumeroPasaporte(input.getNumeroPasaporte());

        return clienteRepository.save(cliente);
    }

    /**
     * Actualizar cliente existente
     */
    public Cliente updateCliente(String id, UpdateClienteInput input) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(CLIENTE_NOT_FOUND_MSG + id));

        // Actualizar solo los campos que no son null
        if (input.getDireccion() != null) {
            cliente.setDireccion(input.getDireccion());
        }
        if (input.getFechaNacimiento() != null && !input.getFechaNacimiento().isEmpty()) {
            cliente.setFechaNacimiento(java.time.LocalDate.parse(input.getFechaNacimiento()));
        }
        if (input.getNumeroPasaporte() != null) {
            // Verificar que el nuevo número de pasaporte no esté duplicado
            Optional<Cliente> clienteWithSamePassport = clienteRepository.findByNumeroPasaporte(input.getNumeroPasaporte());
            if (clienteWithSamePassport.isPresent() && !clienteWithSamePassport.get().getId().equals(id)) {
                throw new RuntimeException("Ya existe otro cliente con ese número de pasaporte");
            }
            cliente.setNumeroPasaporte(input.getNumeroPasaporte());
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Eliminar cliente (hard delete)
     */
    public boolean deleteCliente(String id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(CLIENTE_NOT_FOUND_MSG + id));

        clienteRepository.delete(cliente);
        return true;
    }

    /**
     * Activar/Desactivar cliente
     */
    public Cliente toggleClienteStatus(String id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(CLIENTE_NOT_FOUND_MSG + id));

        // Get associated user and toggle isActive
        Usuario usuario = usuarioRepository.findById(cliente.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario asociado no encontrado"));

        usuario.setIsActive(!usuario.getIsActive());
        usuarioRepository.save(usuario);

        return cliente;
    }
}
