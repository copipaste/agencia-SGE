package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.model.AlertaCancelacion;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.PaqueteTuristico;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.model.Venta;
import com.agencia.agencia_backend.dto.rest.PredictResponseDTO;
import com.agencia.agencia_backend.repository.AlertaCancelacionRepository;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.PaqueteTuristicoRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordatorioService {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioService.class);
    private static final double UMBRAL_RIESGO = 0.70; // 70%

    @Value("${ia.cancelacion.url:http://localhost:8001}")
    private String iaCancelacionUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AlertaCancelacionRepository alertaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PaqueteTuristicoRepository paqueteRepository;

    /**
     * Fuerza el envío de recordatorios llamando al microservicio de IA
     * Este método puede ser llamado desde un endpoint REST o GraphQL
     *
     * @return Mapa con resultado del envío
     */
    public Map<String, Object> forzarEnvioRecordatorios() {
        try {
            log.info("🔔 Forzando envío de recordatorios desde Angular...");

            String url = iaCancelacionUrl + "/recordatorios/enviar";

            // Llamar al endpoint de FastAPI
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> resultado = response.getBody();

                log.info("✅ Recordatorios enviados exitosamente: {}", resultado);

                return Map.of(
                    "success", true,
                    "mensaje", "Recordatorios enviados correctamente",
                    "detalles", resultado
                );
            } else {
                log.warn("⚠️ Respuesta no exitosa de FastAPI: {}", response.getStatusCode());
                return Map.of(
                    "success", false,
                    "mensaje", "Error al enviar recordatorios",
                    "error", "Respuesta no exitosa del microservicio"
                );
            }

        } catch (Exception e) {
            log.error("❌ Error al forzar envío de recordatorios: {}", e.getMessage(), e);
            return Map.of(
                "success", false,
                "mensaje", "Error al conectar con el microservicio de IA",
                "error", e.getMessage()
            );
        }
    }

    /**
     * Obtiene estadísticas de recordatorios desde el microservicio
     *
     * @return Mapa con estadísticas
     */
    public Map<String, Object> obtenerEstadisticasRecordatorios() {
        try {
            String url = iaCancelacionUrl + "/recordatorios/estadisticas";

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                return Map.of(
                    "success", false,
                    "mensaje", "No se pudieron obtener estadísticas"
                );
            }

        } catch (Exception e) {
            log.error("❌ Error al obtener estadísticas: {}", e.getMessage());
            return Map.of(
                "success", false,
                "mensaje", "Error al conectar con el microservicio",
                "error", e.getMessage()
            );
        }
    }

    /**
     * Registra una alerta de cancelación en PostgreSQL
     * Se llama automáticamente al crear una RESERVA con riesgo alto
     */
    @Transactional
    public void registrarAlerta(Venta venta, PredictResponseDTO prediccion) {
        try {
            // Verificar si ya existe alerta para esta venta
            if (alertaRepository.findByVentaId(venta.getId()).isPresent()) {
                log.info("Alerta ya existe para venta: {}", venta.getId());
                return;
            }

            // Solo registrar si probabilidad supera el umbral
            if (prediccion.getProbabilidadCancelacion() < UMBRAL_RIESGO) {
                log.debug("Probabilidad {}% no supera umbral {}%",
                    prediccion.getProbabilidadCancelacion() * 100, UMBRAL_RIESGO * 100);
                return;
            }

            // Obtener datos del cliente
            Cliente cliente = clienteRepository.findById(venta.getClienteId()).orElse(null);
            if (cliente == null) {
                log.warn("Cliente no encontrado: {}", venta.getClienteId());
                return;
            }

            Usuario usuario = usuarioRepository.findById(cliente.getUsuarioId()).orElse(null);
            if (usuario == null) {
                log.warn("Usuario no encontrado: {}", cliente.getUsuarioId());
                return;
            }

            // Obtener datos del paquete (si existe)
            String nombrePaquete = null;
            String destino = null;
            if (venta.getPaqueteId() != null) {
                PaqueteTuristico paquete = paqueteRepository.findById(venta.getPaqueteId()).orElse(null);
                if (paquete != null) {
                    nombrePaquete = paquete.getNombrePaquete();
                    destino = paquete.getDestinoPrincipal();
                }
            }

            // Crear alerta
            AlertaCancelacion alerta = new AlertaCancelacion();
            alerta.setVentaId(venta.getId());
            alerta.setClienteId(venta.getClienteId());
            alerta.setEmailCliente(usuario.getEmail());
            alerta.setNombreCliente(usuario.getNombre() + " " + usuario.getApellido());
            alerta.setNombrePaquete(nombrePaquete);
            alerta.setDestino(destino);
            alerta.setFechaVenta(venta.getFechaVenta());
            alerta.setMontoTotal(venta.getMontoTotal());
            alerta.setProbabilidadCancelacion(prediccion.getProbabilidadCancelacion());
            alerta.setRecomendacion(prediccion.getRecomendacion());
            alerta.setFechaPrediccion(LocalDateTime.now());
            alerta.setRecordatorioEnviado(false);
            alerta.setEstadoVenta(venta.getEstadoVenta());

            alertaRepository.save(alerta);

            log.warn("⚠️ ALERTA REGISTRADA - Venta: {} | Cliente: {} | Prob: {}% | Fecha viaje: {}",
                venta.getId(),
                usuario.getEmail(),
                Math.round(prediccion.getProbabilidadCancelacion() * 100),
                venta.getFechaVenta());

        } catch (Exception e) {
            log.error("Error al registrar alerta: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía recordatorios automáticamente
     * Se ejecuta todos los días a las 10:00 AM
     */
    @Scheduled(cron = "0 0 10 * * *") // Todos los días a las 10:00 AM
    @Transactional
    public void enviarRecordatoriosAutomaticos() {
        log.info("🔔 Iniciando envío automático de recordatorios...");

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime unDiaDespues = ahora.plusDays(1);

        List<AlertaCancelacion> alertas = alertaRepository.findAlertasPendientesProximas(ahora, unDiaDespues);

        log.info("Alertas encontradas: {}", alertas.size());

        int enviados = 0;
        for (AlertaCancelacion alerta : alertas) {
            if (enviarRecordatorio(alerta)) {
                enviados++;
            }
        }

        log.info("✅ Recordatorios enviados: {}/{}", enviados, alertas.size());
    }

    /**
     * Envía recordatorios manualmente (trigger desde Angular)
     * Envía TODOS los recordatorios pendientes sin importar la fecha
     */
    @Transactional
    public int enviarRecordatoriosManuales() {
        log.info("🔔 Iniciando envío MANUAL de recordatorios...");

        List<AlertaCancelacion> alertas = alertaRepository.findAlertasPendientes();

        log.info("Alertas pendientes: {}", alertas.size());

        int enviados = 0;
        for (AlertaCancelacion alerta : alertas) {
            if (enviarRecordatorio(alerta)) {
                enviados++;
            }
        }

        log.info("✅ Recordatorios enviados manualmente: {}/{}", enviados, alertas.size());
        return enviados;
    }

    /**
     * Envía un recordatorio individual por email
     */
    private boolean enviarRecordatorio(AlertaCancelacion alerta) {
        try {
            // Por ahora solo simulamos el envío
            // TODO: Descomentar cuando se configure Spring Mail
            // if (mailSender == null) {
                simularEnvioEmail(alerta);
            // } else {
            //     enviarEmail(alerta);
            // }

            // Actualizar alerta
            alerta.setRecordatorioEnviado(true);
            alerta.setFechaEnvioRecordatorio(LocalDateTime.now());
            alertaRepository.save(alerta);

            log.info("✅ Recordatorio enviado a: {} | Venta: {}",
                alerta.getEmailCliente(), alerta.getVentaId());

            return true;

        } catch (Exception e) {
            log.error("❌ Error al enviar recordatorio a {}: {}",
                alerta.getEmailCliente(), e.getMessage());
            return false;
        }
    }

    /**
     * Envía email real (comentado hasta configurar Spring Mail)
     */
    /*
    private void enviarEmail(AlertaCancelacion alerta) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(alerta.getEmailCliente());
        message.setSubject("🌴 Recordatorio: Tu viaje a " + alerta.getDestino());
        message.setText(construirMensajeEmail(alerta));

        mailSender.send(message);
    }
    */

    /**
     * Simula envío de email (para desarrollo sin SMTP)
     */
    private void simularEnvioEmail(AlertaCancelacion alerta) {
        String mensaje = construirMensajeEmail(alerta);
        log.info("📧 EMAIL SIMULADO:\n{}", mensaje);
    }

    /**
     * Construye el cuerpo del mensaje
     */
    private String construirMensajeEmail(AlertaCancelacion alerta) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaFormateada = alerta.getFechaVenta().format(formatter);

        return String.format(
            "¡Hola %s!\n\n" +
            "Te recordamos que tienes una reserva confirmada:\n\n" +
            "📦 Paquete: %s\n" +
            "📍 Destino: %s\n" +
            "📅 Fecha de viaje: %s\n" +
            "💰 Monto: $%.2f\n\n" +
            "¡Esperamos que disfrutes tu viaje! 🌟\n\n" +
            "Si tienes alguna duda, contáctanos.\n\n" +
            "Saludos,\n" +
            "Agencia de Viajes",
            alerta.getNombreCliente(),
            alerta.getNombrePaquete() != null ? alerta.getNombrePaquete() : "Viaje personalizado",
            alerta.getDestino() != null ? alerta.getDestino() : "Destino especial",
            fechaFormateada,
            alerta.getMontoTotal()
        );
    }

    /**
     * Obtiene estadísticas de alertas
     */
    public Long contarAlertasPendientes() {
        return alertaRepository.countByRecordatorioEnviadoAndEstadoVenta(false, "Pendiente");
    }

    /**
     * Obtiene todas las alertas pendientes
     */
    public List<AlertaCancelacion> obtenerAlertasPendientes() {
        return alertaRepository.findAlertasPendientes();
    }
}

