package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.ReporteVentasDTO;
import com.agencia.agencia_backend.model.Venta;

import com.agencia.agencia_backend.repository.VentaRepository;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReporteVentasService {

    private final VentaRepository ventaRepository;
    private final AgenteService agenteService;
    private final UsuarioRepository usuarioRepository;

    public ReporteVentasService(
            VentaRepository ventaRepository,
            AgenteService agenteService,
            UsuarioRepository usuarioRepository) {
        this.ventaRepository = ventaRepository;
        this.agenteService = agenteService;
        this.usuarioRepository = usuarioRepository;
    }

    public ReporteVentasDTO generarReporte(String periodo) {
        log.info("📊 Generando reporte de ventas para período: {}", periodo);

        // Calcular fechas según el período
        LocalDateTime fechaFin = LocalDateTime.now();
        LocalDateTime fechaInicio;
        
        switch (periodo.toUpperCase()) {
            case "SEMANAL":
                fechaInicio = fechaFin.minus(7, ChronoUnit.DAYS);
                break;
            case "MENSUAL":
                fechaInicio = fechaFin.minus(1, ChronoUnit.MONTHS);
                break;
            case "ANUAL":
                fechaInicio = fechaFin.minus(1, ChronoUnit.YEARS);
                break;
            default:
                throw new IllegalArgumentException("Período no válido. Use: SEMANAL, MENSUAL o ANUAL");
        }

        log.info("📅 Rango de fechas: {} a {}", fechaInicio, fechaFin);

        // Obtener todas las ventas en el rango
        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getFechaVenta() != null)
                .filter(v -> v.getFechaVenta().isAfter(fechaInicio) && v.getFechaVenta().isBefore(fechaFin))
                .collect(Collectors.toList());

        log.info("✅ Se encontraron {} ventas en el período", ventas.size());

        // Calcular estadísticas
        int totalVentas = ventas.size();
        double montoTotal = ventas.stream()
                .mapToDouble(v -> v.getMontoTotal() != null ? v.getMontoTotal() : 0.0)
                .sum();
        double promedioVenta = totalVentas > 0 ? montoTotal / totalVentas : 0.0;

        // Ventas por estado
        List<ReporteVentasDTO.VentaPorEstadoDTO> ventasPorEstado = calcularVentasPorEstado(ventas);

        // Ventas por método de pago
        List<ReporteVentasDTO.VentaPorMetodoPagoDTO> ventasPorMetodoPago = calcularVentasPorMetodoPago(ventas);

        // Top 5 agentes
        List<ReporteVentasDTO.TopAgenteDTO> topAgentes = calcularTopAgentes(ventas);

        // Formatear fechas
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaInicioStr = fechaInicio.format(formatter);
        String fechaFinStr = fechaFin.format(formatter);

        return ReporteVentasDTO.builder()
                .periodo(periodo.toUpperCase())
                .fechaInicio(fechaInicioStr)
                .fechaFin(fechaFinStr)
                .totalVentas(totalVentas)
                .montoTotal(Math.round(montoTotal * 100.0) / 100.0)
                .promedioVenta(Math.round(promedioVenta * 100.0) / 100.0)
                .ventasPorEstado(ventasPorEstado)
                .ventasPorMetodoPago(ventasPorMetodoPago)
                .topAgentes(topAgentes)
                .build();
    }

    private List<ReporteVentasDTO.VentaPorEstadoDTO> calcularVentasPorEstado(List<Venta> ventas) {
        Map<String, List<Venta>> ventasPorEstado = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getEstadoVenta() != null ? v.getEstadoVenta() : "Sin Estado"));

        return ventasPorEstado.entrySet().stream()
                .map(entry -> {
                    String estado = entry.getKey();
                    List<Venta> ventasEstado = entry.getValue();
                    int cantidad = ventasEstado.size();
                    double monto = ventasEstado.stream()
                            .mapToDouble(v -> v.getMontoTotal() != null ? v.getMontoTotal() : 0.0)
                            .sum();
                    
                    return ReporteVentasDTO.VentaPorEstadoDTO.builder()
                            .estado(estado)
                            .cantidad(cantidad)
                            .monto(Math.round(monto * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(ReporteVentasDTO.VentaPorEstadoDTO::getCantidad).reversed())
                .collect(Collectors.toList());
    }

    private List<ReporteVentasDTO.VentaPorMetodoPagoDTO> calcularVentasPorMetodoPago(List<Venta> ventas) {
        Map<String, List<Venta>> ventasPorMetodo = ventas.stream()
                .collect(Collectors.groupingBy(v -> v.getMetodoPago() != null ? v.getMetodoPago() : "Sin Método"));

        return ventasPorMetodo.entrySet().stream()
                .map(entry -> {
                    String metodo = entry.getKey();
                    List<Venta> ventasMetodo = entry.getValue();
                    int cantidad = ventasMetodo.size();
                    double monto = ventasMetodo.stream()
                            .mapToDouble(v -> v.getMontoTotal() != null ? v.getMontoTotal() : 0.0)
                            .sum();
                    
                    return ReporteVentasDTO.VentaPorMetodoPagoDTO.builder()
                            .metodoPago(metodo)
                            .cantidad(cantidad)
                            .monto(Math.round(monto * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(ReporteVentasDTO.VentaPorMetodoPagoDTO::getCantidad).reversed())
                .collect(Collectors.toList());
    }

    private List<ReporteVentasDTO.TopAgenteDTO> calcularTopAgentes(List<Venta> ventas) {
        Map<String, List<Venta>> ventasPorAgente = ventas.stream()
                .filter(v -> v.getAgenteId() != null)
                .collect(Collectors.groupingBy(Venta::getAgenteId));

        return ventasPorAgente.entrySet().stream()
                .map(entry -> {
                    String agenteId = entry.getKey();
                    List<Venta> ventasAgente = entry.getValue();
                    int cantidad = ventasAgente.size();
                    double monto = ventasAgente.stream()
                            .mapToDouble(v -> v.getMontoTotal() != null ? v.getMontoTotal() : 0.0)
                            .sum();

                    // Obtener nombre del agente
                    String agenteNombre = agenteService.getAgenteById(agenteId)
                            .flatMap(agente -> usuarioRepository.findById(agente.getUsuarioId()))
                            .map(usuario -> usuario.getNombre() + " " + usuario.getApellido())
                            .orElse("Agente desconocido");

                    return ReporteVentasDTO.TopAgenteDTO.builder()
                            .agenteId(agenteId)
                            .agenteNombre(agenteNombre)
                            .cantidadVentas(cantidad)
                            .montoTotal(Math.round(monto * 100.0) / 100.0)
                            .build();
                })
                .sorted(Comparator.comparing(ReporteVentasDTO.TopAgenteDTO::getMontoTotal).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}
