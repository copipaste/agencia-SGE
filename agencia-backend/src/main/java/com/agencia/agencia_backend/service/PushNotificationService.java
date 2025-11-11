package com.agencia.agencia_backend.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para enviar notificaciones push usando Firebase Cloud Messaging
 */
@Service
public class PushNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

    /**
     * Verifica si Firebase está inicializado
     */
    private boolean isFirebaseInitialized() {
        return !FirebaseApp.getApps().isEmpty();
    }

    /**
     * Envía notificación cuando se crea una venta desde Angular
     */
    public void enviarNotificacionVentaCreada(String fcmToken, String ventaId, String nombrePaquete, Double monto) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            logger.debug("No se puede enviar notificación: token FCM no disponible");
            return;
        }

        if (!isFirebaseInitialized()) {
            logger.warn("⚠️ Firebase no está inicializado. No se puede enviar notificación.");
            return;
        }

        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "VENTA_CREADA");
            data.put("ventaId", ventaId);
            data.put("nombrePaquete", nombrePaquete != null ? nombrePaquete : "Servicios");
            data.put("monto", String.valueOf(monto));
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle("🎉 Nueva Reserva Registrada")
                            .setBody(nombrePaquete != null
                                ? "Tu reserva para " + nombrePaquete + " ha sido registrada exitosamente"
                                : "Tu reserva ha sido registrada exitosamente")
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("ventas_channel")
                                    .setColor("#4CAF50")
                                    .setIcon("ic_notification")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Notificación de venta creada enviada: {} - Token: {}...", response,
                    fcmToken.substring(0, Math.min(20, fcmToken.length())));
        } catch (FirebaseMessagingException e) {
            logger.error("❌ Error al enviar notificación de venta creada: {}", e.getMessage());
            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT
                    || e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                logger.warn("⚠️ Token FCM inválido o caducado. El cliente debe actualizar su token.");
            }
        }
    }

    /**
     * Envía notificación cuando se confirma una venta desde Angular
     */
    public void enviarNotificacionVentaConfirmada(String fcmToken, String ventaId, String nombrePaquete, Double monto) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            logger.debug("No se puede enviar notificación: token FCM no disponible");
            return;
        }

        if (!isFirebaseInitialized()) {
            logger.warn("⚠️ Firebase no está inicializado. No se puede enviar notificación.");
            return;
        }

        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "VENTA_CONFIRMADA");
            data.put("ventaId", ventaId);
            data.put("nombrePaquete", nombrePaquete != null ? nombrePaquete : "Servicios");
            data.put("monto", String.valueOf(monto));
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle("✅ Reserva Confirmada")
                            .setBody(nombrePaquete != null
                                ? "Tu reserva para " + nombrePaquete + " ha sido confirmada"
                                : "Tu reserva ha sido confirmada")
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("ventas_channel")
                                    .setColor("#2196F3")
                                    .setIcon("ic_notification")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Notificación de venta confirmada enviada: {}", response);
        } catch (FirebaseMessagingException e) {
            logger.error("❌ Error al enviar notificación de venta confirmada: {}", e.getMessage());
        }
    }

    /**
     * Envía notificación cuando se edita una venta desde Angular
     */
    public void enviarNotificacionVentaEditada(String fcmToken, String ventaId, String nombrePaquete, String cambio) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            logger.debug("No se puede enviar notificación: token FCM no disponible");
            return;
        }

        if (!isFirebaseInitialized()) {
            logger.warn("⚠️ Firebase no está inicializado. No se puede enviar notificación.");
            return;
        }

        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "VENTA_EDITADA");
            data.put("ventaId", ventaId);
            data.put("nombrePaquete", nombrePaquete != null ? nombrePaquete : "Servicios");
            data.put("cambio", cambio != null ? cambio : "Actualización de reserva");
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle("📝 Reserva Actualizada")
                            .setBody(nombrePaquete != null
                                ? "Tu reserva para " + nombrePaquete + " ha sido modificada"
                                : "Tu reserva ha sido modificada")
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("ventas_channel")
                                    .setColor("#FF9800")
                                    .setIcon("ic_notification")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Notificación de venta editada enviada: {}", response);
        } catch (FirebaseMessagingException e) {
            logger.error("❌ Error al enviar notificación de venta editada: {}", e.getMessage());
        }
    }

    /**
     * Envía notificación cuando se cancela una venta
     */
    public void enviarNotificacionVentaCancelada(String fcmToken, String ventaId, String nombrePaquete) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            logger.debug("No se puede enviar notificación: token FCM no disponible");
            return;
        }

        if (!isFirebaseInitialized()) {
            logger.warn("⚠️ Firebase no está inicializado. No se puede enviar notificación.");
            return;
        }

        try {
            Map<String, String> data = new HashMap<>();
            data.put("type", "VENTA_CANCELADA");
            data.put("ventaId", ventaId);
            data.put("nombrePaquete", nombrePaquete != null ? nombrePaquete : "Servicios");
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle("❌ Reserva Cancelada")
                            .setBody(nombrePaquete != null
                                ? "Tu reserva para " + nombrePaquete + " ha sido cancelada"
                                : "Tu reserva ha sido cancelada")
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("ventas_channel")
                                    .setColor("#F44336")
                                    .setIcon("ic_notification")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Notificación de venta cancelada enviada: {}", response);
        } catch (FirebaseMessagingException e) {
            logger.error("❌ Error al enviar notificación de venta cancelada: {}", e.getMessage());
        }
    }

    /**
     * Prueba de notificación - útil para testing
     */
    public void enviarNotificacionPrueba(String fcmToken) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            logger.warn("No se puede enviar notificación de prueba: token vacío");
            return;
        }

        if (!isFirebaseInitialized()) {
            logger.warn("⚠️ Firebase no está inicializado. No se puede enviar notificación de prueba.");
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle("🔔 Notificación de Prueba")
                            .setBody("Las notificaciones push están funcionando correctamente")
                            .build())
                    .putData("type", "TEST")
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setChannelId("ventas_channel")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("✅ Notificación de prueba enviada: {}", response);
        } catch (FirebaseMessagingException e) {
            logger.error("❌ Error al enviar notificación de prueba: {}", e.getMessage());
        }
    }
}

