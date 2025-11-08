package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.NotificacionVentaDTO;
import com.agencia.agencia_backend.model.*;
import com.agencia.agencia_backend.repository.VentaRepository;
import com.agencia.agencia_backend.repository.DetalleVentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificacionService {

    @Value("${webhook.n8n.url}")
    private String webhookUrl;

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ClienteService clienteService;
    private final AgenteService agenteService;
    private final ServicioService servicioService;
    private final PaqueteTuristicoService paqueteTuristicoService;
    private final com.agencia.agencia_backend.repository.UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;

    public NotificacionService(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            ClienteService clienteService,
            AgenteService agenteService,
            ServicioService servicioService,
            PaqueteTuristicoService paqueteTuristicoService,
            com.agencia.agencia_backend.repository.UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.clienteService = clienteService;
        this.agenteService = agenteService;
        this.servicioService = servicioService;
        this.paqueteTuristicoService = paqueteTuristicoService;
        this.usuarioRepository = usuarioRepository;
        this.restTemplate = new RestTemplate();
    }

    public NotificacionResponse enviarNotificacion(String ventaId) {
        try {
            log.info("🔔 Iniciando envío de notificación para venta: {}", ventaId);

            // 1. Buscar la venta
            Venta venta = ventaRepository.findById(ventaId)
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            // 2. Obtener el cliente
            Cliente cliente = clienteService.getClienteById(venta.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // 3. Obtener usuario del cliente
            Usuario usuarioCliente = usuarioRepository.findById(cliente.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario del cliente no encontrado"));

            // 4. Validar teléfono
            String telefono = usuarioCliente.getTelefono();
            if (telefono == null || telefono.isEmpty()) {
                throw new RuntimeException("El cliente no tiene teléfono registrado");
            }

            // 5. Obtener agente
            Agente agente = agenteService.getAgenteById(venta.getAgenteId())
                    .orElse(null);

            // 6. Obtener usuario del agente (si existe)
            Usuario usuarioAgente = null;
            if (agente != null) {
                usuarioAgente = usuarioRepository.findById(agente.getUsuarioId()).orElse(null);
            }

            // 7. Obtener detalles
            List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(ventaId);

            // 8. Construir DTO
            NotificacionVentaDTO notificacion = construirNotificacion(venta, usuarioCliente, agente, usuarioAgente, detalles);

            // 7. Enviar al webhook
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<NotificacionVentaDTO> request = new HttpEntity<>(notificacion, headers);

            log.info("📤 Enviando notificación a: {}", webhookUrl);
            log.debug("📋 Datos: {}", notificacion);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    webhookUrl,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Notificación enviada exitosamente para venta: {}", ventaId);
                return NotificacionResponse.builder()
                        .success(true)
                        .message("Notificación enviada exitosamente al cliente")
                        .build();
            } else {
                log.error("❌ Error al enviar notificación. Status: {}", response.getStatusCode());
                throw new RuntimeException("Error al enviar notificación: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("❌ Error al enviar notificación para venta {}: {}", ventaId, e.getMessage(), e);
            return NotificacionResponse.builder()
                    .success(false)
                    .message("Error al enviar notificación: " + e.getMessage())
                    .build();
        }
    }

    private NotificacionVentaDTO construirNotificacion(
            Venta venta, 
            Usuario usuarioCliente, 
            Agente agente, 
            Usuario usuarioAgente, 
            List<DetalleVenta> detalles) {
        
        // Formatear fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = venta.getFechaVenta() != null 
                ? venta.getFechaVenta().format(formatter) 
                : "N/A";

        // Cliente
        String clienteNombre = String.format("%s %s",
                usuarioCliente.getNombre(),
                usuarioCliente.getApellido()
        );

        NotificacionVentaDTO.ClienteNotificacionDTO clienteDTO = NotificacionVentaDTO.ClienteNotificacionDTO.builder()
                .nombre(clienteNombre)
                .telefono(usuarioCliente.getTelefono())
                .email(usuarioCliente.getEmail())
                .build();

        // Agente
        String agenteNombre = usuarioAgente != null
                ? String.format("%s %s",
                        usuarioAgente.getNombre(),
                        usuarioAgente.getApellido())
                : "N/A";

        String agenteCodigo = agente != null
                ? agente.getId() // Usar ID como código ya que no existe campo codigo
                : "N/A";

        NotificacionVentaDTO.AgenteNotificacionDTO agenteDTO = NotificacionVentaDTO.AgenteNotificacionDTO.builder()
                .nombre(agenteNombre)
                .codigo(agenteCodigo)
                .build();

        // Detalles
        List<NotificacionVentaDTO.DetalleNotificacionDTO> detallesDTO = detalles != null
                ? detalles.stream()
                        .map(this::mapearDetalle)
                        .collect(Collectors.toList())
                : List.of();

        // Construir notificación completa
        return NotificacionVentaDTO.builder()
                .ventaId(venta.getId())
                .fechaVenta(fechaFormateada)
                .cliente(clienteDTO)
                .agente(agenteDTO)
                .montoTotal(venta.getMontoTotal())
                .estadoVenta(venta.getEstadoVenta())
                .metodoPago(venta.getMetodoPago())
                .detalles(detallesDTO)
                .build();
    }

    private NotificacionVentaDTO.DetalleNotificacionDTO mapearDetalle(DetalleVenta detalle) {
        String servicioNombre = "Servicio desconocido";
        
        if (detalle.getServicioId() != null) {
            servicioService.getServicioById(detalle.getServicioId())
                    .ifPresent(servicio -> {});
            servicioNombre = servicioService.getServicioById(detalle.getServicioId())
                    .map(Servicio::getTipoServicio)
                    .orElse("Servicio desconocido");
        } else if (detalle.getPaqueteId() != null) {
            servicioNombre = paqueteTuristicoService.getPaqueteTuristicoById(detalle.getPaqueteId())
                    .map(PaqueteTuristico::getNombrePaquete)
                    .orElse("Paquete desconocido");
        }

        return NotificacionVentaDTO.DetalleNotificacionDTO.builder()
                .servicio(servicioNombre)
                .cantidad(detalle.getCantidad())
                .precio(detalle.getPrecioUnitarioVenta())
                .subtotal(detalle.getSubtotal())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NotificacionResponse {
        private Boolean success;
        private String message;
    }
}
