package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.rest.PredictRequestDTO;
import com.agencia.agencia_backend.dto.rest.PredictResponseDTO;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.Venta;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IAIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(IAIntegrationService.class);

    @Value("${ia.cancelacion.url:http://localhost:8001}")
    private String iaCancelacionUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IAFeatureCalculator featureCalculator;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Predice la probabilidad de cancelación de una venta
     * @param ventaId ID de la venta a analizar
     * @return Respuesta del microservicio de IA con probabilidad y recomendación
     */
    public PredictResponseDTO predecirCancelacion(String ventaId) {
        try {
            // 1. Obtener venta de MongoDB
            Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

            // 2. Obtener cliente
            Cliente cliente = clienteRepository.findById(venta.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            // 3. Calcular las 11 features
            PredictRequestDTO request = featureCalculator.calcularFeatures(venta, cliente.getId());

            log.info("Enviando predicción para venta: {} (Cliente: {})", ventaId, cliente.getId());
            log.debug("Features calculadas: {}", request);

            // 4. Llamar al microservicio de IA
            String url = iaCancelacionUrl + "/predict";
            PredictResponseDTO response = restTemplate.postForObject(url, request, PredictResponseDTO.class);

            if (response != null) {
                log.info("Predicción recibida - Probabilidad: {}%, Recomendación: {}",
                    response.getProbabilidadCancelacion() * 100,
                    response.getRecomendacion());
            }

            return response;

        } catch (Exception e) {
            log.error("Error al llamar al microservicio de IA: {}", e.getMessage(), e);
            throw new RuntimeException("Error al predecir cancelación: " + e.getMessage());
        }
    }

    /**
     * Predice cancelación al crear una venta nueva
     * Envía datos completos para que el microservicio pueda guardar alertas
     * @param venta Venta recién creada
     * @param clienteId ID del cliente
     * @return Respuesta del microservicio o null si hay error (no crítico)
     */
    public PredictResponseDTO predecirCancelacionSinFallar(Venta venta, String clienteId) {
        try {
            com.agencia.agencia_backend.dto.rest.PredictRequestFullDTO request =
                featureCalculator.calcularFeaturesCompletas(venta, clienteId);

            // Log detallado para diagnóstico
            log.info("🤖 Enviando predicción COMPLETA a FastAPI:");
            log.info("  - ventaId: {}", request.getVentaId());
            log.info("  - clienteId: {}", request.getClienteId());
            log.info("  - emailCliente: {}", request.getEmailCliente());
            log.info("  - nombreCliente: {}", request.getNombreCliente());
            log.info("  - nombrePaquete: {}", request.getNombrePaquete());
            log.info("  - destino: {}", request.getDestino());
            log.info("  - fechaVenta: {}", request.getFechaVenta());
            log.info("  - montoTotal: {}", request.getMontoTotal());
            log.info("  - Features ML: es_temporada_alta={}, metodo_pago_tarjeta={}, tiene_paquete={}",
                request.getEsTemporadaAlta(), request.getMetodoPagoTarjeta(), request.getTienePaquete());

            String url = iaCancelacionUrl + "/predict";
            PredictResponseDTO response = restTemplate.postForObject(url, request, PredictResponseDTO.class);

            if (response != null) {
                log.info("✅ Respuesta recibida - Probabilidad: {}%",
                    Math.round(response.getProbabilidadCancelacion() * 100));
            }

            return response;
        } catch (Exception e) {
            log.warn("No se pudo predecir cancelación (no crítico): {}", e.getMessage());
            return null;
        }
    }
}

