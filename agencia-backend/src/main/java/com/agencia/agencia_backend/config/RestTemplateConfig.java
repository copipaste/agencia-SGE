package com.agencia.agencia_backend.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Configuración de RestTemplate para comunicación con servicios externos
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, BiServiceConfig biConfig) {
        return builder
            .requestFactory(() -> {
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(biConfig.getTimeout());
                factory.setReadTimeout(biConfig.getTimeout());
                return factory;
            })
            .build();
    }
}
