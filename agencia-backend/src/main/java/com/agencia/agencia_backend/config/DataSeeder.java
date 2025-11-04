package com.agencia.agencia_backend.config;

import com.agencia.agencia_backend.model.Agente;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.repository.AgenteRepository;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AgenteRepository agenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🌱 Iniciando Seeder de datos...");
        
        // Crear Admin
        createAdminUser();
        
        // Crear Agente
        createAgenteUser();
        
        // Crear Cliente
        createClienteUser();
        
        logger.info("✅ Seeder completado exitosamente!");
    }

    /**
     * Crear usuario administrador
     */
    private void createAdminUser() {
        Optional<Usuario> existingAdmin = usuarioRepository.findByEmail("admin@agencia.com");
        
        if (existingAdmin.isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@agencia.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setTelefono("77777777");
            admin.setSexo("M");
            admin.setIsAdmin(true);
            admin.setIsAgente(false);
            admin.setIsCliente(false);
            admin.setIsActive(true);
            
            usuarioRepository.save(admin);
            logger.info("👤 Usuario ADMIN creado: admin@agencia.com / admin123");
        } else {
            logger.info("⚠️  Usuario ADMIN ya existe");
        }
    }

    /**
     * Crear usuario agente con su perfil
     */
    private void createAgenteUser() {
        Optional<Usuario> existingAgente = usuarioRepository.findByEmail("agente@agencia.com");
        
        if (existingAgente.isEmpty()) {
            // Crear usuario
            Usuario usuarioAgente = new Usuario();
            usuarioAgente.setEmail("agente@agencia.com");
            usuarioAgente.setPassword(passwordEncoder.encode("agente123"));
            usuarioAgente.setNombre("Carlos");
            usuarioAgente.setApellido("Rodríguez");
            usuarioAgente.setTelefono("76543210");
            usuarioAgente.setSexo("M");
            usuarioAgente.setIsAdmin(false);
            usuarioAgente.setIsAgente(true);
            usuarioAgente.setIsCliente(false);
            usuarioAgente.setIsActive(true);
            
            Usuario savedAgente = usuarioRepository.save(usuarioAgente);
            
            // Crear perfil de agente
            Agente agente = new Agente();
            agente.setUsuarioId(savedAgente.getId());
            agente.setPuesto("Agente de Ventas Senior");
            agente.setFechaContratacion(LocalDate.of(2024, 1, 15));
            
            agenteRepository.save(agente);
            
            logger.info("👤 Usuario AGENTE creado: agente@agencia.com / agente123");
            logger.info("   - Puesto: Agente de Ventas Senior");
            logger.info("   - Fecha Contratación: 2024-01-15");
        } else {
            logger.info("⚠️  Usuario AGENTE ya existe");
        }
    }

    /**
     * Crear usuario cliente con su perfil
     */
    private void createClienteUser() {
        Optional<Usuario> existingCliente = usuarioRepository.findByEmail("cliente@agencia.com");
        
        if (existingCliente.isEmpty()) {
            // Crear usuario
            Usuario usuarioCliente = new Usuario();
            usuarioCliente.setEmail("cliente@agencia.com");
            usuarioCliente.setPassword(passwordEncoder.encode("cliente123"));
            usuarioCliente.setNombre("María");
            usuarioCliente.setApellido("González");
            usuarioCliente.setTelefono("78901234");
            usuarioCliente.setSexo("F");
            usuarioCliente.setIsAdmin(false);
            usuarioCliente.setIsAgente(false);
            usuarioCliente.setIsCliente(true);
            usuarioCliente.setIsActive(true);
            
            Usuario savedCliente = usuarioRepository.save(usuarioCliente);
            
            // Crear perfil de cliente
            Cliente cliente = new Cliente();
            cliente.setUsuarioId(savedCliente.getId());
            cliente.setDireccion("Av. Cristo Redentor #1234, Santa Cruz, Bolivia");
            cliente.setFechaNacimiento(LocalDate.of(1990, 6, 15));
            cliente.setNumeroPasaporte("BO123456789");
            
            clienteRepository.save(cliente);
            
            logger.info("👤 Usuario CLIENTE creado: cliente@agencia.com / cliente123");
            logger.info("   - Nombre: María González");
            logger.info("   - Dirección: Av. Cristo Redentor #1234, Santa Cruz, Bolivia");
            logger.info("   - Pasaporte: BO123456789");
        } else {
            logger.info("⚠️  Usuario CLIENTE ya existe");
        }
    }
}
