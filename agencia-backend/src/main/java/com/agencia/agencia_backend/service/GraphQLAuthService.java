package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.*;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import com.agencia.agencia_backend.security.JwtUtils;
import com.agencia.agencia_backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GraphQLAuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @Transactional
    public AuthPayload login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        UsuarioDTO usuarioDTO = new UsuarioDTO(
            userDetails.getId(),
            userDetails.getEmail(),
            userDetails.getNombre(),
            userDetails.getApellido(),
            null, // telefono se puede agregar si lo necesitas
            null, // sexo
            userDetails.getIsAdmin(),
            userDetails.getIsAgente(),
            userDetails.getIsCliente(),
            userDetails.getIsActive()
        );
        
        return new AuthPayload(jwt, usuarioDTO);
    }
    
    @Transactional
    public AuthPayload register(RegisterRequest registerRequest) {
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: El email ya está registrado!");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setNombre(registerRequest.getNombre());
        usuario.setApellido(registerRequest.getApellido());
        usuario.setTelefono(registerRequest.getTelefono());
        usuario.setSexo(registerRequest.getSexo());
        usuario.setIsCliente(true); // Por defecto, todo usuario registrado es cliente
        usuario.setIsActive(true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Crear el perfil de cliente
        // Si vienen datos desde REST API (Flutter), usar esos valores
        // Si no, usar valores por defecto "Por completar"
        Cliente cliente = new Cliente();
        cliente.setUsuarioId(usuarioGuardado.getId());
        cliente.setDireccion(
            registerRequest.getDireccion() != null && !registerRequest.getDireccion().isEmpty()
            ? registerRequest.getDireccion()
            : "Por completar"
        );
        cliente.setNumeroPasaporte(
            registerRequest.getNumeroPasaporte() != null && !registerRequest.getNumeroPasaporte().isEmpty()
            ? registerRequest.getNumeroPasaporte()
            : "Por completar"
        );

        // Parsear fecha de nacimiento si se proporciona
        if (registerRequest.getFechaNacimiento() != null && !registerRequest.getFechaNacimiento().isEmpty()) {
            try {
                cliente.setFechaNacimiento(java.time.LocalDate.parse(registerRequest.getFechaNacimiento()));
            } catch (Exception e) {
                // Si el formato es inválido, dejar null
                cliente.setFechaNacimiento(null);
            }
        }

        clienteRepository.save(cliente);
        
        // Autenticar automáticamente
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UsuarioDTO usuarioDTO = new UsuarioDTO(
            usuarioGuardado.getId(),
            usuarioGuardado.getEmail(),
            usuarioGuardado.getNombre(),
            usuarioGuardado.getApellido(),
            usuarioGuardado.getTelefono(),
            usuarioGuardado.getSexo(),
            usuarioGuardado.getIsAdmin(),
            usuarioGuardado.getIsAgente(),
            usuarioGuardado.getIsCliente(),
            usuarioGuardado.getIsActive()
        );
        
        return new AuthPayload(jwt, usuarioDTO);
    }
    
    public UsuarioDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
            || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Usuario no autenticado");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return new UsuarioDTO(
            userDetails.getId(),
            userDetails.getEmail(),
            userDetails.getNombre(),
            userDetails.getApellido(),
            null,
            null,
            userDetails.getIsAdmin(),
            userDetails.getIsAgente(),
            userDetails.getIsCliente(),
            userDetails.getIsActive()
        );
    }
}
