package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.CreateVentaInput;
import com.agencia.agencia_backend.dto.UpdateVentaInput;
import com.agencia.agencia_backend.model.Agente;
import com.agencia.agencia_backend.model.Cliente;
import com.agencia.agencia_backend.model.DetalleVenta;
import com.agencia.agencia_backend.model.PaqueteTuristico;
import com.agencia.agencia_backend.model.Servicio;
import com.agencia.agencia_backend.model.Venta;
import com.agencia.agencia_backend.service.AgenteService;
import com.agencia.agencia_backend.service.ClienteService;
import com.agencia.agencia_backend.service.NotificacionService;
import com.agencia.agencia_backend.service.PaqueteTuristicoService;
import com.agencia.agencia_backend.service.ServicioService;
import com.agencia.agencia_backend.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class VentaResolver {

    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private AgenteService agenteService;
    
    @Autowired
    private ServicioService servicioService;
    
    @Autowired
    private PaqueteTuristicoService paqueteTuristicoService;
    
    @Autowired
    private NotificacionService notificacionService;

    // QUERIES

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public List<Venta> getAllVentas() {
        return ventaService.getAllVentas();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE', 'CLIENTE')")
    public Venta getVentaById(@Argument String id) {
        return ventaService.getVentaById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + id));
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public List<Venta> getVentasByClienteId(@Argument String clienteId) {
        return ventaService.getVentasByClienteId(clienteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public List<Venta> getVentasByAgenteId(@Argument String agenteId) {
        return ventaService.getVentasByAgenteId(agenteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public List<Venta> getVentasByEstado(@Argument String estado) {
        return ventaService.getVentasByEstado(estado);
    }

    // MUTATIONS

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public Venta createVenta(@Argument CreateVentaInput input) {
        return ventaService.createVenta(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public Venta updateVenta(@Argument String id, @Argument UpdateVentaInput input) {
        return ventaService.updateVenta(id, input)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + id));
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteVenta(@Argument String id) {
        return ventaService.deleteVenta(id);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public NotificacionService.NotificacionResponse notificarVenta(@Argument String ventaId) {
        return notificacionService.enviarNotificacion(ventaId);
    }

    // FIELD RESOLVERS

    @SchemaMapping(typeName = "Venta", field = "cliente")
    public Cliente cliente(Venta venta) {
        return clienteService.getClienteById(venta.getClienteId()).orElse(null);
    }

    @SchemaMapping(typeName = "Venta", field = "agente")
    public Agente agente(Venta venta) {
        return agenteService.getAgenteById(venta.getAgenteId()).orElse(null);
    }

    @SchemaMapping(typeName = "Venta", field = "detalles")
    public List<DetalleVenta> detalles(Venta venta) {
        return ventaService.getDetallesByVentaId(venta.getId());
    }

    @SchemaMapping(typeName = "DetalleVenta", field = "servicio")
    public Servicio servicio(DetalleVenta detalleVenta) {
        if (detalleVenta.getServicioId() != null) {
            return servicioService.getServicioById(detalleVenta.getServicioId()).orElse(null);
        }
        return null;
    }

    @SchemaMapping(typeName = "DetalleVenta", field = "paquete")
    public PaqueteTuristico paquete(DetalleVenta detalleVenta) {
        if (detalleVenta.getPaqueteId() != null) {
            return paqueteTuristicoService.getPaqueteTuristicoById(detalleVenta.getPaqueteId()).orElse(null);
        }
        return null;
    }
}
