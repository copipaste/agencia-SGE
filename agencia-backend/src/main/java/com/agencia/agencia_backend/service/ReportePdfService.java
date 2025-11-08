package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.ReporteVentasDTO;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ReportePdfService {

    public byte[] generarReportePdf(ReporteVentasDTO reporte) {
        log.info("📄 Generando PDF del reporte de ventas");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Colores corporativos
            DeviceRgb colorPrimario = new DeviceRgb(102, 126, 234); // #667eea
            DeviceRgb colorSecundario = new DeviceRgb(118, 75, 162); // #764ba2

            // Título principal
            Paragraph titulo = new Paragraph("REPORTE DE VENTAS")
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(colorPrimario)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(titulo);

            // Subtítulo con período
            Paragraph subtitulo = new Paragraph("Período: " + reporte.getPeriodo())
                    .setFontSize(16)
                    .setFontColor(colorSecundario)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitulo);

            // Información del período
            document.add(new Paragraph("Fecha de inicio: " + reporte.getFechaInicio())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Fecha de fin: " + reporte.getFechaFin())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            // Fecha de generación
            String fechaGeneracion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            document.add(new Paragraph("Generado el: " + fechaGeneracion)
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Resumen general
            document.add(new Paragraph("RESUMEN GENERAL")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(colorPrimario)
                    .setMarginBottom(10));

            Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .useAllAvailableWidth();

            // Headers del resumen
            resumenTable.addCell(crearCeldaHeader("Total Ventas"));
            resumenTable.addCell(crearCeldaHeader("Monto Total"));
            resumenTable.addCell(crearCeldaHeader("Promedio por Venta"));

            // Datos del resumen
            resumenTable.addCell(crearCeldaDato(String.valueOf(reporte.getTotalVentas())));
            resumenTable.addCell(crearCeldaDato(String.format("$%.2f", reporte.getMontoTotal())));
            resumenTable.addCell(crearCeldaDato(String.format("$%.2f", reporte.getPromedioVenta())));

            document.add(resumenTable);
            document.add(new Paragraph("\n"));

            // Ventas por Estado
            if (reporte.getVentasPorEstado() != null && !reporte.getVentasPorEstado().isEmpty()) {
                document.add(new Paragraph("VENTAS POR ESTADO")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(colorPrimario)
                        .setMarginBottom(10));

                Table estadoTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2}))
                        .useAllAvailableWidth();

                estadoTable.addCell(crearCeldaHeader("Estado"));
                estadoTable.addCell(crearCeldaHeader("Cantidad"));
                estadoTable.addCell(crearCeldaHeader("Monto"));

                for (ReporteVentasDTO.VentaPorEstadoDTO estado : reporte.getVentasPorEstado()) {
                    estadoTable.addCell(crearCeldaDato(getEstadoIcon(estado.getEstado()) + " " + estado.getEstado()));
                    estadoTable.addCell(crearCeldaDato(String.valueOf(estado.getCantidad())));
                    estadoTable.addCell(crearCeldaDato(String.format("$%.2f", estado.getMonto())));
                }

                document.add(estadoTable);
                document.add(new Paragraph("\n"));
            }

            // Ventas por Método de Pago
            if (reporte.getVentasPorMetodoPago() != null && !reporte.getVentasPorMetodoPago().isEmpty()) {
                document.add(new Paragraph("VENTAS POR MÉTODO DE PAGO")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(colorPrimario)
                        .setMarginBottom(10));

                Table metodoTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2}))
                        .useAllAvailableWidth();

                metodoTable.addCell(crearCeldaHeader("Método de Pago"));
                metodoTable.addCell(crearCeldaHeader("Cantidad"));
                metodoTable.addCell(crearCeldaHeader("Monto"));

                for (ReporteVentasDTO.VentaPorMetodoPagoDTO metodo : reporte.getVentasPorMetodoPago()) {
                    metodoTable.addCell(crearCeldaDato(getMetodoPagoIcon(metodo.getMetodoPago()) + " " + metodo.getMetodoPago()));
                    metodoTable.addCell(crearCeldaDato(String.valueOf(metodo.getCantidad())));
                    metodoTable.addCell(crearCeldaDato(String.format("$%.2f", metodo.getMonto())));
                }

                document.add(metodoTable);
                document.add(new Paragraph("\n"));
            }

            // Top Agentes
            if (reporte.getTopAgentes() != null && !reporte.getTopAgentes().isEmpty()) {
                document.add(new Paragraph("TOP AGENTES DEL PERÍODO")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(colorPrimario)
                        .setMarginBottom(10));

                Table agenteTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2}))
                        .useAllAvailableWidth();

                agenteTable.addCell(crearCeldaHeader("Agente"));
                agenteTable.addCell(crearCeldaHeader("Ventas"));
                agenteTable.addCell(crearCeldaHeader("Monto Total"));

                for (ReporteVentasDTO.TopAgenteDTO agente : reporte.getTopAgentes()) {
                    agenteTable.addCell(crearCeldaDato(agente.getAgenteNombre()));
                    agenteTable.addCell(crearCeldaDato(String.valueOf(agente.getCantidadVentas())));
                    agenteTable.addCell(crearCeldaDato(String.format("$%.2f", agente.getMontoTotal())));
                }

                document.add(agenteTable);
            }

            // Footer
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Agencia de Viajes - Sistema de Gestión")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            document.close();
            log.info("✅ PDF generado exitosamente");

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("❌ Error al generar PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar el reporte PDF", e);
        }
    }

    private Cell crearCeldaHeader(String texto) {
        return new Cell()
                .add(new Paragraph(texto).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(new DeviceRgb(102, 126, 234))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
    }

    private Cell crearCeldaDato(String texto) {
        return new Cell()
                .add(new Paragraph(texto))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }

    private String getEstadoIcon(String estado) {
        switch (estado) {
            case "Pendiente": return "⏳";
            case "Confirmada": return "✅";
            case "Cancelada": return "❌";
            case "Completada": return "🎉";
            default: return "📋";
        }
    }

    private String getMetodoPagoIcon(String metodo) {
        switch (metodo) {
            case "Efectivo": return "💵";
            case "Tarjeta": return "💳";
            case "Transferencia": return "🏦";
            default: return "💰";
        }
    }
}
