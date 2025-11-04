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

    // Obtener todas las ventas
    public List<Venta> getAllVentas() {
        return ventaRepository.findAll();
    }

    // Obtener venta por ID
    public Optional<Venta> getVentaById(String id) {
        return ventaRepository.findById(id);
    }

    // Obtener ventas por cliente
    public List<Venta> getVentasByClienteId(String clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    // Obtener ventas por agente
    public List<Venta> getVentasByAgenteId(String agenteId) {
        return ventaRepository.findByAgenteId(agenteId);
    }

    // Obtener ventas por estado
    public List<Venta> getVentasByEstado(String estado) {
        return ventaRepository.findByEstadoVenta(estado);
    }

    // Crear nueva venta con detalles
    public Venta createVenta(CreateVentaInput input) {
        System.out.println("=== Creando nueva venta ===");
        
        // Validar que el cliente existe
        if (!clienteRepository.existsById(input.getClienteId())) {
            throw new RuntimeException("Cliente no encontrado con id: " + input.getClienteId());
        }
        
        // Validar que el agente existe
        if (!agenteRepository.existsById(input.getAgenteId())) {
            throw new RuntimeException("Agente no encontrado con id: " + input.getAgenteId());
        }
        
        // Crear la venta
        Venta venta = new Venta();
        venta.setClienteId(input.getClienteId());
        venta.setAgenteId(input.getAgenteId());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstadoVenta(input.getEstadoVenta());
        venta.setMetodoPago(input.getMetodoPago());
        
        // Calcular monto total desde los detalles
        double montoTotal = calcularMontoTotal(input.getDetalles());
        venta.setMontoTotal(montoTotal);
        
        Venta savedVenta = ventaRepository.save(venta);
        System.out.println("Venta guardada con ID: " + savedVenta.getId());
        
        // Crear los detalles de venta
        if (input.getDetalles() != null && !input.getDetalles().isEmpty()) {
            createDetallesVenta(savedVenta.getId(), input.getDetalles());
        }
        
        return savedVenta;
    }

    // Actualizar venta
    public Optional<Venta> updateVenta(String id, UpdateVentaInput input) {
        return ventaRepository.findById(id).map(venta -> {
            if (input.getEstadoVenta() != null) {
                venta.setEstadoVenta(input.getEstadoVenta());
            }
            if (input.getMetodoPago() != null) {
                venta.setMetodoPago(input.getMetodoPago());
            }
            
            // Si se actualizan los detalles, recalcular el monto total
            if (input.getDetalles() != null) {
                // Eliminar detalles existentes
                detalleVentaRepository.deleteByVentaId(id);
                // Crear nuevos detalles
                createDetallesVenta(id, input.getDetalles());
                // Recalcular monto total
                double montoTotal = calcularMontoTotal(input.getDetalles());
                venta.setMontoTotal(montoTotal);
            }
            
            return ventaRepository.save(venta);
        });
    }

    // Eliminar venta (y sus detalles)
    public boolean deleteVenta(String id) {
        if (ventaRepository.existsById(id)) {
            // Eliminar primero los detalles
            detalleVentaRepository.deleteByVentaId(id);
            // Luego eliminar la venta
            ventaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener detalles de una venta
    public List<DetalleVenta> getDetallesByVentaId(String ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }

    // Método auxiliar para crear detalles de venta
    private void createDetallesVenta(String ventaId, List<DetalleVentaItemInput> detallesInput) {
        System.out.println("=== Creando detalles de venta para ventaId: " + ventaId + " ===");
        
        for (DetalleVentaItemInput item : detallesInput) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVentaId(ventaId);
            detalle.setServicioId(item.getServicioId());
            detalle.setPaqueteId(item.getPaqueteId());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitarioVenta(item.getPrecioUnitarioVenta());
            
            // Calcular subtotal
            double subtotal = item.getCantidad() * item.getPrecioUnitarioVenta();
            detalle.setSubtotal(subtotal);
            
            DetalleVenta saved = detalleVentaRepository.save(detalle);
            System.out.println("✓ DetalleVenta guardado con ID: " + saved.getId());
        }
        
        System.out.println("=== Finalizado creación de detalles ===");
    }

    // Método auxiliar para calcular monto total
    private double calcularMontoTotal(List<DetalleVentaItemInput> detalles) {
        return detalles.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitarioVenta())
                .sum();
    }
}
