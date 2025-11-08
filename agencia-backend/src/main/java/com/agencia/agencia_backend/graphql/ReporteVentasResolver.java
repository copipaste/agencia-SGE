package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.ReporteVentasDTO;
import com.agencia.agencia_backend.service.ReporteVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ReporteVentasResolver {

    @Autowired
    private ReporteVentasService reporteVentasService;

    @QueryMapping
    public ReporteVentasDTO getReporteVentas(@Argument String periodo) {
        return reporteVentasService.generarReporte(periodo);
    }
}
