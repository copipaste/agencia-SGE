package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.rest.ApiResponse;
import com.agencia.agencia_backend.dto.rest.ConfirmarVentaRequest;
import com.agencia.agencia_backend.dto.rest.CreateVentaRequest;
import com.agencia.agencia_backend.dto.rest.VentaDTO;
import com.agencia.agencia_backend.dto.rest.VentaDetalleDTO;
import com.agencia.agencia_backend.service.VentaRestService;
import com.agencia.agencia_backend.service.GraphQLAuthService;
import com.agencia.agencia_backend.dto.UsuarioDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VentaController {

    @Autowired
    private VentaRestService ventaService;

    @Autowired
    private GraphQLAuthService authService;

    /**
     * POST /api/ventas
     * Crea una nueva venta/reserva para el cliente autenticado
     * Asigna automáticamente un agente existente al azar
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VentaDTO>> crearVenta(@Valid @RequestBody CreateVentaRequest request) {
        try {
            // Obtener usuario autenticado
            UsuarioDTO usuario = authService.getCurrentUser();

            // Verificar que es cliente
            if (!usuario.getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado. Solo clientes pueden crear ventas.", null));
            }

            // Crear la venta
            VentaDTO venta = ventaService.crearVentaParaCliente(request, usuario.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Venta creada exitosamente", venta));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al crear venta: " + e.getMessage(), null));
        }
    }

    /**
     * GET /api/ventas/mias
     * Lista las ventas del cliente autenticado
     * Filtro opcional por estado
     */
    @GetMapping("/mias")
    public ResponseEntity<ApiResponse<List<VentaDTO>>> listarMisVentas(
            @RequestParam(required = false) String estado) {
        try {
            // Obtener usuario autenticado
            UsuarioDTO usuario = authService.getCurrentUser();

            // Verificar que es cliente
            if (!usuario.getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado. Solo clientes pueden ver sus ventas.", null));
            }

            // Listar ventas del cliente
            List<VentaDTO> ventas = ventaService.listarVentasDeCliente(usuario.getId(), estado);

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Ventas obtenidas exitosamente", ventas)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al obtener ventas: " + e.getMessage(), null));
        }
    }

    /**
     * GET /api/ventas/{id}
     * Obtiene el detalle de una venta específica
     * Solo si pertenece al cliente autenticado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaDetalleDTO>> obtenerDetalleVenta(@PathVariable String id) {
        try {
            // Obtener usuario autenticado
            UsuarioDTO usuario = authService.getCurrentUser();

            // Verificar que es cliente
            if (!usuario.getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado.", null));
            }

            // Obtener detalle de la venta
            VentaDetalleDTO venta = ventaService.obtenerVentaDeCliente(id, usuario.getId());

            if (venta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Venta no encontrada o no pertenece al cliente", null));
            }

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Detalle de venta obtenido exitosamente", venta)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al obtener detalle de venta: " + e.getMessage(), null));
        }
    }

    /**
     * PATCH /api/ventas/{id}/cancelar
     * Cancela una venta solo si está en estado Pendiente
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<VentaDTO>> cancelarVenta(@PathVariable String id) {
        try {
            // Obtener usuario autenticado
            UsuarioDTO usuario = authService.getCurrentUser();

            // Verificar que es cliente
            if (!usuario.getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado.", null));
            }

            // Cancelar la venta
            VentaDTO venta = ventaService.cancelarVentaDeCliente(id, usuario.getId());

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Venta cancelada exitosamente", venta)
            );

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al cancelar venta: " + e.getMessage(), null));
        }
    }

    /**
     * PATCH /api/ventas/{id}/confirmar
     * Confirma una venta que está en estado Pendiente
     * Opcionalmente permite especificar el método de pago
     */
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ApiResponse<VentaDetalleDTO>> confirmarVenta(
            @PathVariable String id,
            @RequestBody(required = false) ConfirmarVentaRequest request) {
        try {
            // Obtener usuario autenticado
            UsuarioDTO usuario = authService.getCurrentUser();

            // Verificar que es cliente
            if (!usuario.getIsCliente()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Acceso denegado.", null));
            }

            // Obtener método de pago del request (puede ser null)
            String metodoPago = (request != null && request.getMetodoPago() != null)
                ? request.getMetodoPago()
                : "TARJETA"; // Por defecto TARJETA

            // Confirmar la venta
            VentaDetalleDTO venta = ventaService.confirmarVentaDeCliente(id, usuario.getId(), metodoPago);

            return ResponseEntity.ok(
                new ApiResponse<>(true, "Venta confirmada exitosamente", venta)
            );

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al confirmar venta: " + e.getMessage(), null));
        }
    }
}

