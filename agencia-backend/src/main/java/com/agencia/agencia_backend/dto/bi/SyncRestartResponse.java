package com.agencia.agencia_backend.dto.bi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para la respuesta del endpoint /sync/restart del servicio BI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncRestartResponse {
    private String status;
    private String message;
}
