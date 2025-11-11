package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.rest.PredictRequestDTO;
import com.agencia.agencia_backend.dto.rest.PredictRequestFullDTO;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.PaqueteTuristico;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.model.Venta;
import com.agencia.agencia_backend.repository.ClienteRepository;
import com.agencia.agencia_backend.repository.PaqueteTuristicoRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import com.agencia.agencia_backend.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IAFeatureCalculator {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PaqueteTuristicoRepository paqueteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Calcula las 11 features necesarias para la predicción
     */
    public PredictRequestDTO calcularFeatures(Venta venta, String clienteId) {
        PredictRequestDTO dto = new PredictRequestDTO();

        dto.setVentaId(venta.getId());
        dto.setClienteId(clienteId);

        // ===== FEATURES DE VENTA (7) =====
        dto.setMontoTotal(venta.getMontoTotal());
        dto.setEsTemporadaAlta(esTemporadaAlta(venta.getFechaVenta()) ? 1 : 0);
        dto.setDiaSemanaReserva(venta.getFechaVenta().getDayOfWeek().getValue() - 1);
        dto.setMetodoPagoTarjeta("TARJETA".equals(venta.getMetodoPago()) ? 1 : 0);
        dto.setTienePaquete(venta.getPaqueteId() != null ? 1 : 0);

        // Del paquete (si existe)
        if (venta.getPaqueteId() != null) {
            PaqueteTuristico paquete = paqueteRepository.findById(venta.getPaqueteId()).orElse(null);
            if (paquete != null) {
                dto.setDuracionDias(paquete.getDuracionDias());
                dto.setDestinoCategoria(clasificarDestino(paquete.getDestinoPrincipal()));
            } else {
                dto.setDuracionDias(5);
                dto.setDestinoCategoria(2);
            }
        } else {
            dto.setDuracionDias(5);
            dto.setDestinoCategoria(2);
        }

        // ===== FEATURES DE CLIENTE (4) =====
        calcularFeaturesCliente(dto, clienteId);

        return dto;
    }

    /**
     * Calcula features COMPLETAS incluyendo datos para recordatorios
     * Este método se usa cuando se crea una RESERVA (para sistema de alertas)
     */
    public PredictRequestFullDTO calcularFeaturesCompletas(Venta venta, String clienteId) {
        PredictRequestFullDTO dto = new PredictRequestFullDTO();

        // IDs
        dto.setVentaId(venta.getId());
        dto.setClienteId(clienteId);

        // ===== DATOS PARA RECORDATORIOS =====
        // Obtener cliente y usuario
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente != null) {
            Usuario usuario = usuarioRepository.findById(cliente.getUsuarioId()).orElse(null);
            if (usuario != null) {
                dto.setEmailCliente(usuario.getEmail());
                dto.setNombreCliente(usuario.getNombre() + " " + usuario.getApellido());
            }
        }

        // Datos de paquete
        if (venta.getPaqueteId() != null) {
            PaqueteTuristico paquete = paqueteRepository.findById(venta.getPaqueteId()).orElse(null);
            if (paquete != null) {
                dto.setNombrePaquete(paquete.getNombrePaquete());
                dto.setDestino(paquete.getDestinoPrincipal());
                dto.setDuracionDias(paquete.getDuracionDias());
                dto.setDestinoCategoria(clasificarDestino(paquete.getDestinoPrincipal()));
            } else {
                dto.setDuracionDias(5);
                dto.setDestinoCategoria(2);
            }
        } else {
            dto.setDuracionDias(5);
            dto.setDestinoCategoria(2);
        }

        dto.setFechaVenta(venta.getFechaVenta());
        dto.setMontoTotal(venta.getMontoTotal());

        // ===== FEATURES ML (11) =====
        dto.setEsTemporadaAlta(esTemporadaAlta(venta.getFechaVenta()) ? 1 : 0);
        dto.setDiaSemanaReserva(venta.getFechaVenta().getDayOfWeek().getValue() - 1);
        dto.setMetodoPagoTarjeta("TARJETA".equals(venta.getMetodoPago()) ? 1 : 0);
        dto.setTienePaquete(venta.getPaqueteId() != null ? 1 : 0);

        // Features de cliente
        List<Venta> historial = ventaRepository.findByClienteId(clienteId);
        dto.setTotalComprasPrevias(historial.size());

        long cancelaciones = historial.stream()
                .filter(v -> "Cancelada".equals(v.getEstadoVenta()))
                .count();
        dto.setTotalCancelacionesPrevias((int) cancelaciones);

        dto.setTasaCancelacionHistorica(
                historial.size() > 0 ? (double) cancelaciones / historial.size() : 0.0
        );

        double montoPromedio = historial.stream()
                .mapToDouble(Venta::getMontoTotal)
                .average()
                .orElse(0.0);
        dto.setMontoPromedioCompras(montoPromedio);

        return dto;
    }

    /**
     * Calcula features del historial del cliente
     */
    private void calcularFeaturesCliente(PredictRequestDTO dto, String clienteId) {
        List<Venta> historial = ventaRepository.findByClienteId(clienteId);

        // Total de compras previas
        dto.setTotalComprasPrevias(historial.size());

        // Total de cancelaciones previas
        long cancelaciones = historial.stream()
                .filter(v -> "Cancelada".equals(v.getEstadoVenta()))
                .count();
        dto.setTotalCancelacionesPrevias((int) cancelaciones);

        // Tasa de cancelación histórica
        dto.setTasaCancelacionHistorica(
                historial.size() > 0 ? (double) cancelaciones / historial.size() : 0.0
        );

        // Monto promedio de compras
        double montoPromedio = historial.stream()
                .mapToDouble(Venta::getMontoTotal)
                .average()
                .orElse(0.0);
        dto.setMontoPromedioCompras(montoPromedio);
    }

    /**
     * Determina si una fecha está en temporada alta
     * Temporada alta: Julio (7), Agosto (8), Diciembre (12)
     */
    private boolean esTemporadaAlta(LocalDateTime fecha) {
        int mes = fecha.getMonthValue();
        return mes == 7 || mes == 8 || mes == 12;
    }

    /**
     * Clasifica el destino en categorías
     * 0 = Playa/Costa
     * 1 = Ciudad/Urbano
     * 2 = Aventura/Montaña/Otro
     */
    private int clasificarDestino(String destino) {
        String d = destino.toLowerCase();

        // Playa/Costa
        if (d.contains("playa") || d.contains("cancún") || d.contains("mar") ||
                d.contains("caribe") || d.contains("costa") || d.contains("isla")) {
            return 0;
        }

        // Ciudad/Urbano
        if (d.contains("ciudad") || d.contains("urbano") || d.contains("la paz") ||
                d.contains("cochabamba") || d.contains("santa cruz")) {
            return 1;
        }

        // Aventura/Montaña/Otro
        return 2;
    }
}

