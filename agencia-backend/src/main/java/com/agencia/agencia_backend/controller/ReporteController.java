package com.agencia.agencia_backend.controller;

import com.agencia.agencia_backend.dto.ReporteVentasDTO;
import com.agencia.agencia_backend.service.ReportePdfService;
import com.agencia.agencia_backend.service.ReporteVentasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteVentasService reporteVentasService;
    private final ReportePdfService reportePdfService;

    @GetMapping("/ventas/pdf")
    public ResponseEntity<byte[]> generarReportePdf(
            @RequestParam(defaultValue = "MENSUAL") String periodo) {
        
        log.info("📊 Solicitud de reporte PDF - Período: {}", periodo);

        try {
            // Generar el reporte con datos
            ReporteVentasDTO reporte = reporteVentasService.generarReporte(periodo);

            // Generar el PDF
            byte[] pdfBytes = reportePdfService.generarReportePdf(reporte);

            // Configurar headers para descarga
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                    "reporte-ventas-" + periodo.toLowerCase() + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            log.info("✅ PDF generado correctamente - Tamaño: {} bytes", pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("❌ Error al generar reporte PDF: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
