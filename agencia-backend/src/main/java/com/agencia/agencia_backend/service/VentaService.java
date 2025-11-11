package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.CreateVentaInput;
import com.agencia.agencia_backend.dto.DetalleVentaItemInput;
import com.agencia.agencia_backend.dto.UpdateVentaInput;
import com.agencia.agencia_backend.model.DetalleVenta;
import com.agencia.agencia_backend.model.Venta;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.AgenteRepository;
import com.agencia.agencia_backend.repository.DetalleVentaRepository;
import com.agencia.agencia_backend.repository.VentaRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AgenteRepository agenteRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Venta> getAllVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> getVentaById(String id) {
        return ventaRepository.findById(id);
    }

    public List<Venta> getVentasByClienteId(String clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    public List<Venta> getVentasByAgenteId(String agenteId) {
        return ventaRepository.findByAgenteId(agenteId);
    }

    public List<Venta> getVentasByEstado(String estado) {
        return ventaRepository.findByEstadoVenta(estado);
    }

    public Venta createVenta(CreateVentaInput input) {
        System.out.println("=== Creando nueva venta ===");

        if (!clienteRepository.existsById(input.getClienteId())) {
            throw new RuntimeException("Cliente no encontrado con id: " + input.getClienteId());
        }

        if (!agenteRepository.existsById(input.getAgenteId())) {
            throw new RuntimeException("Agente no encontrado con id: " + input.getAgenteId());
        }

        Venta venta = new Venta();
        venta.setClienteId(input.getClienteId());
        venta.setAgenteId(input.getAgenteId());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstadoVenta(input.getEstadoVenta());
        venta.setMetodoPago(input.getMetodoPago());
        venta.setMontoTotal(calcularMontoTotal(input.getDetalles()));

        Venta savedVenta = ventaRepository.save(venta);
        System.out.println("Venta guardada con ID: " + savedVenta.getId());

        if (input.getDetalles() != null && !input.getDetalles().isEmpty()) {
            createDetallesVenta(savedVenta.getId(), input.getDetalles());
        }

        enviarNotificacionNuevaVenta(savedVenta, input.getDetalles());

        return savedVenta;
    }

    public Optional<Venta> updateVenta(String id, UpdateVentaInput input) {
        return ventaRepository.findById(id).map(venta -> {
            String estadoAnterior = venta.getEstadoVenta();

            if (input.getEstadoVenta() != null) {
                venta.setEstadoVenta(input.getEstadoVenta());
            }
            if (input.getMetodoPago() != null) {
                venta.setMetodoPago(input.getMetodoPago());
            }

            if (input.getDetalles() != null) {
                detalleVentaRepository.deleteByVentaId(id);
                createDetallesVenta(id, input.getDetalles());
                venta.setMontoTotal(calcularMontoTotal(input.getDetalles()));
            }

            Venta ventaActualizada = ventaRepository.save(venta);

            if (input.getEstadoVenta() != null && !input.getEstadoVenta().equals(estadoAnterior)) {
                if ("Confirmada".equals(input.getEstadoVenta())) {
                    enviarNotificacionVentaConfirmada(ventaActualizada);
                } else if ("Cancelada".equals(input.getEstadoVenta())) {
                    enviarNotificacionVentaCancelada(ventaActualizada);
                }
            } else if (input.getDetalles() != null) {
                enviarNotificacionVentaEditada(ventaActualizada);
            }

            return ventaActualizada;
        });
    }

    public boolean deleteVenta(String id) {
        if (ventaRepository.existsById(id)) {
            detalleVentaRepository.deleteByVentaId(id);
            ventaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DetalleVenta> getDetallesByVentaId(String ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }

    private void createDetallesVenta(String ventaId, List<DetalleVentaItemInput> detallesInput) {
        System.out.println("=== Creando detalles de venta para ventaId: " + ventaId + " ===");

        for (DetalleVentaItemInput item : detallesInput) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVentaId(ventaId);
            detalle.setServicioId(item.getServicioId());
            detalle.setPaqueteId(item.getPaqueteId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitarioVenta(item.getPrecioUnitarioVenta());
            detalle.setSubtotal(item.getCantidad() * item.getPrecioUnitarioVenta());

            DetalleVenta saved = detalleVentaRepository.save(detalle);
            System.out.println("✓ DetalleVenta guardado con ID: " + saved.getId());
        }

        System.out.println("=== Finalizado creación de detalles ===");
    }

    private double calcularMontoTotal(List<DetalleVentaItemInput> detalles) {
        return detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitarioVenta())
                .sum();
    }

    private void enviarNotificacionNuevaVenta(Venta venta, List<DetalleVentaItemInput> detalles) {
        try {
            clienteRepository.findById(venta.getClienteId()).ifPresent(cliente -> {
                usuarioRepository.findById(cliente.getUsuarioId()).ifPresent(usuario -> {
                    if (usuario.getFcmToken() != null && !usuario.getFcmToken().isEmpty()) {
                        String nombrePaquete = obtenerNombrePaquete(detalles);
                        pushNotificationService.enviarNotificacionVentaCreada(
                                usuario.getFcmToken(),
                                venta.getId(),
                                nombrePaquete,
                                venta.getMontoTotal()
                        );
                        System.out.println("🔔 Notificación enviada al cliente: " + usuario.getEmail());
                    }
                });
            });
        } catch (Exception e) {
            System.err.println("❌ Error al enviar notificación: " + e.getMessage());
        }
    }

    private void enviarNotificacionVentaConfirmada(Venta venta) {
        try {
            clienteRepository.findById(venta.getClienteId()).ifPresent(cliente -> {
                usuarioRepository.findById(cliente.getUsuarioId()).ifPresent(usuario -> {
                    if (usuario.getFcmToken() != null && !usuario.getFcmToken().isEmpty()) {
                        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(venta.getId());
                        String nombrePaquete = obtenerNombrePaqueteDesdeDetalles(detalles);
                        pushNotificationService.enviarNotificacionVentaConfirmada(
                                usuario.getFcmToken(),
                                venta.getId(),
                                nombrePaquete,
                                venta.getMontoTotal()
                        );
                    }
                });
            });
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private void enviarNotificacionVentaEditada(Venta venta) {
        try {
            clienteRepository.findById(venta.getClienteId()).ifPresent(cliente -> {
                usuarioRepository.findById(cliente.getUsuarioId()).ifPresent(usuario -> {
                    if (usuario.getFcmToken() != null && !usuario.getFcmToken().isEmpty()) {
                        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(venta.getId());
                        String nombrePaquete = obtenerNombrePaqueteDesdeDetalles(detalles);
                        pushNotificationService.enviarNotificacionVentaEditada(
                                usuario.getFcmToken(),
                                venta.getId(),
                                nombrePaquete,
                                "Detalles actualizados"
                        );
                    }
                });
            });
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private void enviarNotificacionVentaCancelada(Venta venta) {
        try {
            clienteRepository.findById(venta.getClienteId()).ifPresent(cliente -> {
                usuarioRepository.findById(cliente.getUsuarioId()).ifPresent(usuario -> {
                    if (usuario.getFcmToken() != null && !usuario.getFcmToken().isEmpty()) {
                        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(venta.getId());
                        String nombrePaquete = obtenerNombrePaqueteDesdeDetalles(detalles);
                        pushNotificationService.enviarNotificacionVentaCancelada(
                                usuario.getFcmToken(),
                                venta.getId(),
                                nombrePaquete
                        );
                    }
                });
            });
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private String obtenerNombrePaquete(List<DetalleVentaItemInput> detalles) {
        if (detalles == null || detalles.isEmpty()) return null;
        for (DetalleVentaItemInput detalle : detalles) {
            if (detalle.getPaqueteId() != null) return "Paquete Turístico";
        }
        return "Servicios personalizados";
    }

    private String obtenerNombrePaqueteDesdeDetalles(List<DetalleVenta> detalles) {
        if (detalles == null || detalles.isEmpty()) return null;
        for (DetalleVenta detalle : detalles) {
            if (detalle.getPaqueteId() != null) return "Paquete Turístico";
        }
        return "Servicios personalizados";
    }
}
