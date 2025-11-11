package com.agencia.agencia_backend.repository;

import com.agencia.agencia_backend.model.AlertaCancelacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaCancelacionRepository extends MongoRepository<AlertaCancelacion, String> {

    /**
     * Busca alerta por ventaId
     */
    Optional<AlertaCancelacion> findByVentaId(String ventaId);

    /**
     * Busca alertas pendientes de envío cuya fecha de viaje está próxima
     * (entre ahora y 1 día en el futuro)
     */
    @Query("{ 'recordatorioEnviado': false, 'estadoVenta': 'Pendiente', 'fechaVenta': { $gte: ?0, $lte: ?1 } }")
    List<AlertaCancelacion> findAlertasPendientesProximas(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Busca todas las alertas pendientes de envío (sin filtro de fecha)
     * Útil para el trigger manual desde Angular
     */
    @Query("{ 'recordatorioEnviado': false, 'estadoVenta': 'Pendiente' }")
    List<AlertaCancelacion> findAlertasPendientes();

    /**
     * Cuenta alertas pendientes
     */
    Long countByRecordatorioEnviadoAndEstadoVenta(Boolean recordatorioEnviado, String estadoVenta);
}

