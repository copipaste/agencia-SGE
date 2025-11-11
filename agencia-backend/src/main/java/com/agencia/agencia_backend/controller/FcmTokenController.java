package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.rest.ApiResponse;
import com.agencia.agencia_backend.model.Usuario;
import com.agencia.agencia_backend.repository.UsuarioRepository;
import com.agencia.agencia_backend.service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST para gestionar tokens FCM de notificaciones push
 */
@RestController
@RequestMapping("/api/fcm")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FcmTokenController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PushNotificationService pushNotificationService;

    /**
     * Actualiza el token FCM del usuario autenticado
     * POST /api/fcm/token
     */
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<Map<String, String>>> actualizarTokenFcm(
            @RequestBody Map<String, String> body) {
        try {
            // Obtener usuario autenticado desde el contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            String fcmToken = body.get("fcmToken");

            if (fcmToken == null || fcmToken.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Token FCM requerido", null));
            }

            // Buscar y actualizar usuario
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setFcmToken(fcmToken);
            usuarioRepository.save(usuario);

            System.out.println("✅ Token FCM actualizado para: " + email);
            System.out.println("   Token: " + fcmToken.substring(0, Math.min(20, fcmToken.length())) + "...");

            return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Token FCM actualizado correctamente",
                Map.of("email", email, "tokenLength", String.valueOf(fcmToken.length()))
            ));
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar token FCM: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al actualizar token FCM: " + e.getMessage(), null));
        }
    }

    /**
     * Envía una notificación de prueba al usuario autenticado
     * POST /api/fcm/test
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<String>> enviarNotificacionPrueba() {
        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (usuario.getFcmToken() == null || usuario.getFcmToken().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Usuario no tiene token FCM registrado", null));
            }

            // Enviar notificación de prueba
            pushNotificationService.enviarNotificacionPrueba(usuario.getFcmToken());

            return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Notificación de prueba enviada correctamente",
                "Revisa tu dispositivo móvil"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al enviar notificación: " + e.getMessage(), null));
        }
    }

    /**
     * Elimina el token FCM del usuario autenticado (para logout)
     * DELETE /api/fcm/token
     */
    @DeleteMapping("/token")
    public ResponseEntity<ApiResponse<String>> eliminarTokenFcm() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setFcmToken(null);
            usuarioRepository.save(usuario);

            System.out.println("🗑️ Token FCM eliminado para: " + email);

            return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Token FCM eliminado correctamente",
                "El usuario ya no recibirá notificaciones push"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Error al eliminar token FCM: " + e.getMessage(), null));
        }
    }
}

