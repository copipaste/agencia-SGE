package com.agencia.agencia_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Configuración de Firebase Cloud Messaging para notificaciones push
 */
@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Buscar el archivo de credenciales en resources
                ClassPathResource resource = new ClassPathResource("firebase-service-account.json");

                if (!resource.exists()) {
                    logger.warn("⚠️ Archivo firebase-service-account.json no encontrado. Las notificaciones push NO funcionarán.");
                    logger.warn("📝 Para habilitar FCM:");
                    logger.warn("   1. Descarga firebase-service-account.json desde Firebase Console");
                    logger.warn("   2. Colócalo en src/main/resources/");
                    logger.warn("   3. Reinicia la aplicación");
                    return;
                }

                InputStream serviceAccount = resource.getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                logger.info("✅ Firebase Admin SDK inicializado correctamente");
                logger.info("🔔 Sistema de notificaciones push activo");
            }
        } catch (IOException e) {
            logger.error("❌ Error al inicializar Firebase Admin SDK: {}", e.getMessage());
            logger.error("🔔 Las notificaciones push NO funcionarán hasta resolver este error");
        }
    }
}

