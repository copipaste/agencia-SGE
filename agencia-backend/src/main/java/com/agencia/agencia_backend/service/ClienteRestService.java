package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.rest.ActualizarPerfilRequest;
import com.agencia.agencia_backend.dto.rest.ClientePerfilDTO;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ClienteRestService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene el perfil completo del cliente (usuario + cliente)
     */
    public ClientePerfilDTO obtenerPerfilCompleto(String usuarioId) {
        // Obtener usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Obtener cliente
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado para este usuario"));

        // Construir DTO
        ClientePerfilDTO perfil = new ClientePerfilDTO();
        perfil.setUsuarioId(usuario.getId());
        perfil.setEmail(usuario.getEmail());
        perfil.setNombre(usuario.getNombre());
        perfil.setApellido(usuario.getApellido());
        perfil.setTelefono(usuario.getTelefono());
        perfil.setSexo(usuario.getSexo());

        perfil.setClienteId(cliente.getId());
        perfil.setDireccion(cliente.getDireccion());
        perfil.setFechaNacimiento(cliente.getFechaNacimiento() != null ?
            cliente.getFechaNacimiento().toString() : null);
        perfil.setNumeroPasaporte(cliente.getNumeroPasaporte());

        return perfil;
    }

    /**
     * Actualiza el perfil del cliente
     */
    @Transactional
    public ClientePerfilDTO actualizarPerfil(String usuarioId, ActualizarPerfilRequest request) {
        // Obtener usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Obtener cliente
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Actualizar datos del Usuario (solo si se proporcionan)
        if (request.getNombre() != null && !request.getNombre().isEmpty()) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getApellido() != null && !request.getApellido().isEmpty()) {
            usuario.setApellido(request.getApellido());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getSexo() != null) {
            usuario.setSexo(request.getSexo());
        }

        // Actualizar datos del Cliente (solo si se proporcionan)
        if (request.getDireccion() != null && !request.getDireccion().isEmpty()) {
            cliente.setDireccion(request.getDireccion());
        }
        if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()) {
            try {
                LocalDate fecha = LocalDate.parse(request.getFechaNacimiento(), DateTimeFormatter.ISO_DATE);
                cliente.setFechaNacimiento(fecha);
            } catch (Exception e) {
                throw new IllegalArgumentException("Formato de fecha inválido. Use formato ISO: YYYY-MM-DD");
            }
        }
        if (request.getNumeroPasaporte() != null && !request.getNumeroPasaporte().isEmpty()) {
            cliente.setNumeroPasaporte(request.getNumeroPasaporte());
        }

        // Guardar cambios
        usuarioRepository.save(usuario);
        clienteRepository.save(cliente);

        // Retornar perfil actualizado
        return obtenerPerfilCompleto(usuarioId);
    }
}

