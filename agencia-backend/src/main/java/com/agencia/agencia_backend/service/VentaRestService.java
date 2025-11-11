package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.rest.AgenteBasicoDTO;
import com.agencia.agencia_backend.dto.rest.ClienteBasicoDTO;
import com.agencia.agencia_backend.dto.rest.CreateVentaRequest;
import com.agencia.agencia_backend.dto.rest.PaqueteDetalladoDTO;
import com.agencia.agencia_backend.dto.rest.PaqueteTuristicoDTO;
import com.agencia.agencia_backend.dto.rest.ServicioDTO;
import com.agencia.agencia_backend.dto.rest.VentaDTO;
import com.agencia.agencia_backend.dto.rest.VentaDetalleDTO;
import com.agencia.agencia_backend.model.Agente;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.DetalleVenta;
import com.agencia.agencia_backend.model.PaqueteTuristico;
import com.agencia.agencia_backend.model.Servicio;
import com.agencia.agencia_backend.model.Venta;
import com.agencia.agencia_backend.repository.AgenteRepository;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.DetalleVentaRepository;
import com.agencia.agencia_backend.repository.PaqueteServicioRepository;
import com.agencia.agencia_backend.repository.PaqueteTuristicoRepository;
import com.agencia.agencia_backend.repository.ServicioRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import com.agencia.agencia_backend.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class VentaRestService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private AgenteRepository agenteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaqueteServicioRepository paqueteServicioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private PaqueteTuristicoRepository paqueteRepository;

    @Autowired
    private IAIntegrationService iaService;


    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    private final Random random = new Random();

    /**
     * Crea una nueva venta para un cliente
     * Asigna automáticamente un agente al azar
     */
    @Transactional
    public VentaDTO crearVentaParaCliente(CreateVentaRequest request, String usuarioId) {
        // 1. Obtener cliente del usuario autenticado
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado para el usuario"));

        // 2. Validar que el paquete existe
        PaqueteTuristico paquete = paqueteRepository.findById(request.getPaqueteId())
            .orElseThrow(() -> new IllegalArgumentException("Paquete turístico no encontrado"));

        // 3. Obtener un agente al azar
        String agenteId = obtenerAgenteAleatorio();

        // 4. Validar modo
        if (!request.getModo().equals("RESERVA") && !request.getModo().equals("COMPRA")) {
            throw new IllegalArgumentException("Modo inválido. Debe ser 'RESERVA' o 'COMPRA'");
        }

        // 5. Parsear fecha de inicio desde Flutter
        // Esta es la fecha en que el cliente comenzará su viaje (paquete)
        LocalDateTime fechaVenta;
        try {
            LocalDate fecha = LocalDate.parse(request.getFechaInicio(), DateTimeFormatter.ISO_DATE);
            fechaVenta = fecha.atStartOfDay();
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use formato ISO: YYYY-MM-DD");
        }

        // 6. Crear la venta
        Venta venta = new Venta();
        venta.setClienteId(cliente.getId());
        venta.setAgenteId(agenteId);
        venta.setPaqueteId(paquete.getId());
        venta.setFechaVenta(fechaVenta);
        venta.setMontoTotal(paquete.getPrecioTotalVenta());

        // Asignar método de pago según el modo
        if (request.getModo().equals("COMPRA")) {
            venta.setMetodoPago("Tarjeta"); // Pago con tarjeta desde app móvil
            venta.setEstadoVenta("Confirmada");
        } else {
            venta.setMetodoPago("Pendiente"); // Reserva sin pago aún
            venta.setEstadoVenta("Pendiente");
        }

        // 7. Guardar venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // 8. Crear DetalleVenta (consistencia con GraphQL/Angular)
        // Esto asegura que Angular pueda leer las ventas creadas desde Flutter
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVentaId(ventaGuardada.getId());
        detalle.setPaqueteId(paquete.getId());
        detalle.setServicioId(null); // Móvil solo vende paquetes completos
        detalle.setCantidad(1); // Siempre 1 paquete desde móvil
        detalle.setPrecioUnitarioVenta(paquete.getPrecioTotalVenta());
        detalle.setSubtotal(paquete.getPrecioTotalVenta());
        detalleVentaRepository.save(detalle);

        // 9. ✅ NUEVO: Predicción automática de cancelación (solo para RESERVAS)
        if ("RESERVA".equals(request.getModo()) && "Pendiente".equals(ventaGuardada.getEstadoVenta())) {
            ejecutarPrediccionAutomatica(ventaGuardada, cliente.getId());
        }

        // 10. Convertir a DTO y devolver
        return convertirAVentaDTO(ventaGuardada);
    }

    /**
     * Ejecuta predicción automática de cancelación
     * El microservicio de IA se encarga de registrar alertas y enviar recordatorios
     */
    private void ejecutarPrediccionAutomatica(Venta venta, String clienteId) {
        try {
            // log.info("🤖 Ejecutando predicción automática para venta: {}", venta.getId());

            com.agencia.agencia_backend.dto.rest.PredictResponseDTO prediccion =
                iaService.predecirCancelacionSinFallar(venta, clienteId);

            if (prediccion != null) {
                // log.info("Predicción recibida - Probabilidad: {}%, Recomendación: {}",
                //     Math.round(prediccion.getProbabilidadCancelacion() * 100),
                //     prediccion.getRecomendacion());

                // El microservicio de IA maneja las alertas y recordatorios
                if (prediccion.getProbabilidadCancelacion() > 0.70) {
                    // log.warn("⚠️ ALTO RIESGO de cancelación detectado para venta: {}", venta.getId());
                }
            }
        } catch (Exception e) {
            // log.error("Error en predicción automática (no crítico): {}", e.getMessage());
        }
    }

    /**
     * Obtiene un agente existente al azar
     */
    private String obtenerAgenteAleatorio() {
        List<Agente> agentes = agenteRepository.findAll();

        if (agentes.isEmpty()) {
            throw new IllegalStateException("No hay agentes disponibles en el sistema. Contacte al administrador.");
        }

        // Seleccionar uno al azar
        int indiceAleatorio = random.nextInt(agentes.size());
        return agentes.get(indiceAleatorio).getId();
    }

    /**
     * Lista las ventas de un cliente con filtro opcional por estado
     */
    public List<VentaDTO> listarVentasDeCliente(String usuarioId, String estado) {
        // Obtener cliente del usuario
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Obtener ventas del cliente
        List<Venta> ventas = ventaRepository.findByClienteId(cliente.getId());

        // Filtrar por estado si se especifica
        if (estado != null && !estado.isEmpty()) {
            ventas = ventas.stream()
                .filter(v -> v.getEstadoVenta().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
        }

        // Convertir a DTOs
        return ventas.stream()
            .map(this::convertirAVentaDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene el detalle de una venta específica del cliente
     */
    public VentaDetalleDTO obtenerVentaDeCliente(String ventaId, String usuarioId) {
        // Obtener cliente del usuario
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Obtener venta
        Venta venta = ventaRepository.findById(ventaId).orElse(null);

        if (venta == null) {
            return null;
        }

        // Verificar que la venta pertenece al cliente
        if (!venta.getClienteId().equals(cliente.getId())) {
            throw new SecurityException("Esta venta no pertenece al cliente autenticado");
        }

        // Convertir a DTO detallado (el método se encarga de buscar el paquete)
        return convertirAVentaDetalleDTO(venta);
    }

    /**
     * Cancela una venta si está en estado Pendiente
     */
    @Transactional
    public VentaDTO cancelarVentaDeCliente(String ventaId, String usuarioId) {
        // Obtener cliente del usuario
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Obtener venta
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        // Verificar que la venta pertenece al cliente
        if (!venta.getClienteId().equals(cliente.getId())) {
            throw new SecurityException("Esta venta no pertenece al cliente autenticado");
        }

        // Verificar que está en estado Pendiente
        if (!"Pendiente".equalsIgnoreCase(venta.getEstadoVenta())) {
            throw new IllegalStateException("La venta no puede cancelarse porque ya está confirmada.");
        }

        // Cancelar la venta
        venta.setEstadoVenta("Cancelada");
        Venta ventaActualizada = ventaRepository.save(venta);

        // Convertir a DTO
        return convertirAVentaDTO(ventaActualizada);
    }

    /**
     * Confirma una venta si está en estado Pendiente
     */
    @Transactional
    public VentaDetalleDTO confirmarVentaDeCliente(String ventaId, String usuarioId, String metodoPago) {
        // Obtener cliente del usuario
        Cliente cliente = clienteRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        // Obtener venta
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        // Verificar que la venta pertenece al cliente
        if (!venta.getClienteId().equals(cliente.getId())) {
            throw new SecurityException("Esta venta no pertenece al cliente autenticado");
        }

        // Verificar que está en estado Pendiente
        if (!"Pendiente".equalsIgnoreCase(venta.getEstadoVenta())) {
            throw new IllegalStateException("Solo se pueden confirmar ventas con estado Pendiente");
        }

        // Confirmar la venta
        venta.setEstadoVenta("Confirmada");

        // Actualizar método de pago
        if (metodoPago != null && !metodoPago.isEmpty()) {
            venta.setMetodoPago(metodoPago);
        } else {
            // Por defecto, si se confirma sin especificar, se asume pago con tarjeta
            venta.setMetodoPago("Tarjeta");
        }

        Venta ventaActualizada = ventaRepository.save(venta);

        // Convertir a DTO detallado con todos los datos
        return convertirAVentaDetalleDTO(ventaActualizada);
    }

    /**
     * Convierte una Venta a VentaDTO (para listado)
     */
    private VentaDTO convertirAVentaDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setId(venta.getId());
        dto.setClienteId(venta.getClienteId());
        dto.setAgenteId(venta.getAgenteId());
        dto.setFechaVenta(venta.getFechaVenta().toString());
        dto.setMontoTotal(venta.getMontoTotal());
        dto.setEstadoVenta(venta.getEstadoVenta());
        dto.setMetodoPago(venta.getMetodoPago());

        // Obtener cliente completo
        dto.setCliente(obtenerClienteBasico(venta.getClienteId()));

        // Obtener agente completo
        dto.setAgente(obtenerAgenteBasico(venta.getAgenteId()));

        // ✅ NUEVO: Obtener nombrePaquete de los detalles
        dto.setNombrePaquete(obtenerNombrePaqueteDeVenta(venta.getId()));

        return dto;
    }

    /**
     * Convierte una Venta a VentaDetalleDTO con toda la información
     */
    private VentaDetalleDTO convertirAVentaDetalleDTO(Venta venta) {
        VentaDetalleDTO dto = new VentaDetalleDTO();
        dto.setId(venta.getId());
        dto.setClienteId(venta.getClienteId());
        dto.setAgenteId(venta.getAgenteId());
        dto.setFechaVenta(venta.getFechaVenta().toString());
        dto.setMontoTotal(venta.getMontoTotal());
        dto.setEstadoVenta(venta.getEstadoVenta());
        dto.setMetodoPago(venta.getMetodoPago());

        // Obtener cliente completo
        dto.setCliente(obtenerClienteBasico(venta.getClienteId()));

        // Obtener agente completo
        dto.setAgente(obtenerAgenteBasico(venta.getAgenteId()));

        // Obtener detalles de la venta (igual que GraphQL)
        List<com.agencia.agencia_backend.dto.rest.DetalleVentaDTO> detalles = obtenerDetallesVenta(venta.getId());
        dto.setDetalles(detalles);

        return dto;
    }

    /**
     * Obtiene el cliente básico con datos del usuario
     */
    private ClienteBasicoDTO obtenerClienteBasico(String clienteId) {
        return clienteRepository.findById(clienteId)
            .map(cliente -> {
                ClienteBasicoDTO dto = new ClienteBasicoDTO();
                dto.setId(cliente.getId());
                dto.setUsuarioId(cliente.getUsuarioId());

                // Obtener datos del usuario
                usuarioRepository.findById(cliente.getUsuarioId()).ifPresent(usuario -> {
                    dto.setNombre(usuario.getNombre());
                    dto.setApellido(usuario.getApellido());
                    dto.setEmail(usuario.getEmail());
                    dto.setTelefono(usuario.getTelefono());
                });

                return dto;
            })
            .orElse(null);
    }

    /**
     * Obtiene el agente básico con datos del usuario
     */
    private AgenteBasicoDTO obtenerAgenteBasico(String agenteId) {
        return agenteRepository.findById(agenteId)
            .map(agente -> {
                AgenteBasicoDTO dto = new AgenteBasicoDTO();
                dto.setId(agente.getId());
                dto.setUsuarioId(agente.getUsuarioId());

                // Obtener datos del usuario
                usuarioRepository.findById(agente.getUsuarioId()).ifPresent(usuario -> {
                    dto.setNombre(usuario.getNombre());
                    dto.setApellido(usuario.getApellido());
                    dto.setEmail(usuario.getEmail());
                    dto.setTelefono(usuario.getTelefono());
                });

                return dto;
            })
            .orElse(null);
    }

    /**
     * Obtiene los detalles de una venta (igual que GraphQL)
     */
    private List<com.agencia.agencia_backend.dto.rest.DetalleVentaDTO> obtenerDetallesVenta(String ventaId) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(ventaId);

        return detalles.stream()
            .map(this::convertirDetalleVentaADTO)
            .collect(Collectors.toList());
    }

    /**
     * Convierte un DetalleVenta a DetalleVentaDTO
     */
    private com.agencia.agencia_backend.dto.rest.DetalleVentaDTO convertirDetalleVentaADTO(DetalleVenta detalle) {
        com.agencia.agencia_backend.dto.rest.DetalleVentaDTO dto = new com.agencia.agencia_backend.dto.rest.DetalleVentaDTO();
        dto.setId(detalle.getId());
        dto.setVentaId(detalle.getVentaId());
        dto.setServicioId(detalle.getServicioId());
        dto.setPaqueteId(detalle.getPaqueteId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitarioVenta(detalle.getPrecioUnitarioVenta());
        dto.setSubtotal(detalle.getSubtotal());

        // Si tiene servicioId, obtener el servicio completo
        if (detalle.getServicioId() != null) {
            servicioRepository.findById(detalle.getServicioId()).ifPresent(servicio -> {
                dto.setServicio(convertirServicioADTO(servicio));
            });
        }

        // Si tiene paqueteId, obtener el paquete completo
        if (detalle.getPaqueteId() != null) {
            paqueteRepository.findById(detalle.getPaqueteId()).ifPresent(paquete -> {
                dto.setPaquete(convertirPaqueteADTO(paquete));
            });
        }

        return dto;
    }

    /**
     * Convierte un Servicio a ServicioDTO
     */
    private ServicioDTO convertirServicioADTO(Servicio servicio) {
        return new ServicioDTO(
            servicio.getId(),
            servicio.getProveedorId(),
            servicio.getTipoServicio(),
            servicio.getNombreServicio(),
            servicio.getDescripcion(),
            servicio.getDestinoCiudad(),
            servicio.getDestinoPais(),
            servicio.getPrecioCosto(),
            servicio.getPrecioVenta(),
            servicio.getIsAvailable()
        );
    }

    /**
     * Convierte un PaqueteTuristico a PaqueteDetalladoDTO con servicios expandidos
     */
    private PaqueteDetalladoDTO convertirPaqueteADTO(PaqueteTuristico paquete) {
        // Obtener IDs de servicios del paquete
        List<String> serviciosIds = paqueteServicioRepository.findAll().stream()
            .filter(ps -> ps.getPaqueteId().equals(paquete.getId()))
            .map(ps -> ps.getServicioId())
            .collect(Collectors.toList());

        // ✅ EXPANDIR servicios: convertir IDs a objetos ServicioDTO completos
        List<ServicioDTO> servicios = serviciosIds.stream()
            .map(servicioId -> servicioRepository.findById(servicioId).orElse(null))
            .filter(servicio -> servicio != null)
            .map(this::convertirServicioADTO)
            .collect(Collectors.toList());

        return new PaqueteDetalladoDTO(
            paquete.getId(),
            paquete.getNombrePaquete(),
            paquete.getDescripcion(),
            paquete.getDestinoPrincipal(),
            paquete.getDuracionDias(),
            paquete.getPrecioTotalVenta(),
            servicios // ✅ Servicios completos, NO solo IDs
        );
    }

    /**
     * Obtiene el nombre del paquete de una venta (busca en los detalles)
     */
    private String obtenerNombrePaqueteDeVenta(String ventaId) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(ventaId);

        for (DetalleVenta detalle : detalles) {
            if (detalle.getPaqueteId() != null) {
                return paqueteRepository.findById(detalle.getPaqueteId())
                    .map(PaqueteTuristico::getNombrePaquete)
                    .orElse(null);
            }
        }

        return null; // Si no tiene paquete, retornar null
    }
}

