package com.agencia.agencia_backend.dto.bi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para la respuesta del endpoint /sync/status del servicio BI
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncStatusResponse {
    
    @JsonProperty("sync_enabled")
    private Boolean syncEnabled;
    
    @JsonProperty("sync_running")
    private Boolean syncRunning;
    
    private String message;
}
