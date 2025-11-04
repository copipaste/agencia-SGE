package com.agencia.agencia_backend.graphql;

import com.agencia.agencia_backend.dto.CreatePaqueteTuristicoInput;
import com.agencia.agencia_backend.dto.UpdatePaqueteTuristicoInput;
import com.agencia.agencia_backend.model.PaqueteTuristico;
import com.agencia.agencia_backend.model.Servicio;
import com.agencia.agencia_backend.service.PaqueteTuristicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PaqueteTuristicoResolver {

    @Autowired
    private PaqueteTuristicoService paqueteTuristicoService;

    // QUERIES

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE', 'CLIENTE')")
    public List<PaqueteTuristico> getAllPaquetesTuristicos() {
        return paqueteTuristicoService.getAllPaquetesTuristicos();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE', 'CLIENTE')")
    public PaqueteTuristico getPaqueteTuristicoById(@Argument String id) {
        return paqueteTuristicoService.getPaqueteTuristicoById(id)
                .orElseThrow(() -> new RuntimeException("Paquete turístico no encontrado con id: " + id));
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE', 'CLIENTE')")
    public List<PaqueteTuristico> getPaquetesTuristicosByDestino(@Argument String destino) {
        return paqueteTuristicoService.getPaquetesTuristicosByDestino(destino);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE', 'CLIENTE')")
    public List<PaqueteTuristico> searchPaquetesTuristicos(@Argument String keyword) {
        return paqueteTuristicoService.searchPaquetesTuristicos(keyword);
    }

    // MUTATIONS

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public PaqueteTuristico createPaqueteTuristico(@Argument CreatePaqueteTuristicoInput input) {
        return paqueteTuristicoService.createPaqueteTuristico(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public PaqueteTuristico updatePaqueteTuristico(@Argument String id, @Argument UpdatePaqueteTuristicoInput input) {
        return paqueteTuristicoService.updatePaqueteTuristico(id, input)
                .orElseThrow(() -> new RuntimeException("Paquete turístico no encontrado con id: " + id));
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deletePaqueteTuristico(@Argument String id) {
        return paqueteTuristicoService.deletePaqueteTuristico(id);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public Boolean addServicioToPaquete(@Argument String paqueteId, @Argument String servicioId) {
        return paqueteTuristicoService.addServicioToPaquete(paqueteId, servicioId);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENTE')")
    public Boolean removeServicioFromPaquete(@Argument String paqueteId, @Argument String servicioId) {
        return paqueteTuristicoService.removeServicioFromPaquete(paqueteId, servicioId);
    }

    // FIELD RESOLVERS

    @SchemaMapping(typeName = "PaqueteTuristico", field = "servicios")
    public List<Servicio> servicios(PaqueteTuristico paqueteTuristico) {
        return paqueteTuristicoService.getServiciosByPaqueteId(paqueteTuristico.getId());
    }
}
