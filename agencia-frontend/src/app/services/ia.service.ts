import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * GraphQL Mutation para forzar el envío de recordatorios
 */
const FORZAR_ENVIO_RECORDATORIOS = gql`
  mutation {
    forzarEnvioRecordatorios
  }
`;

/**
 * GraphQL Query para obtener estadísticas de recordatorios
 */
const ESTADISTICAS_RECORDATORIOS = gql`
  query {
    estadisticasRecordatorios
  }
`;

/**
 * Servicio para interactuar con el microservicio de IA
 * a través de GraphQL (Spring Boot)
 * 
 * @description
 * Este servicio permite:
 * - Forzar el envío de recordatorios de cancelación a clientes
 * - Obtener estadísticas sobre predicciones y recordatorios
 * 
 * NOTA: Solo usuarios con rol AGENTE pueden ejecutar estas operaciones
 */
@Injectable({
  providedIn: 'root'
})
export class IAService {

  constructor(private apollo: Apollo) {}

  /**
   * Fuerza el envío inmediato de recordatorios de cancelación
   * a los clientes con alta probabilidad de cancelar
   * 
   * @returns Observable con el resultado del envío
   * 
   * @example
   * ```typescript
   * this.iaService.forzarEnvioRecordatorios().subscribe({
   *   next: (resultado) => {
   *     if (resultado.success) {
   *       console.log(`Enviados: ${resultado.detalles.recordatorios_enviados}`);
   *     }
   *   },
   *   error: (error) => console.error('Error:', error)
   * });
   * ```
   */
  forzarEnvioRecordatorios(): Observable<any> {
    return this.apollo.mutate({
      mutation: FORZAR_ENVIO_RECORDATORIOS
    }).pipe(
      map((result: any) => {
        const jsonString = result.data.forzarEnvioRecordatorios;
        // El backend ahora retorna un String con JSON, necesitamos parsearlo
        return JSON.parse(jsonString);
      })
    );
  }

  /**
   * Obtiene estadísticas actuales de recordatorios
   * 
   * @returns Observable con las estadísticas
   * 
   * @example
   * ```typescript
   * this.iaService.obtenerEstadisticasRecordatorios().subscribe({
   *   next: (stats) => {
   *     console.log('Total predicciones:', stats.total_predicciones);
   *     console.log('Pendientes:', stats.recordatorios_pendientes);
   *     console.log('Enviados:', stats.recordatorios_enviados);
   *   }
   * });
   * ```
   */
  obtenerEstadisticasRecordatorios(): Observable<any> {
    return this.apollo.query({
      query: ESTADISTICAS_RECORDATORIOS,
      fetchPolicy: 'network-only' // Siempre obtener datos frescos
    }).pipe(
      map((result: any) => {
        const jsonString = result.data.estadisticasRecordatorios;
        // El backend ahora retorna un String con JSON, necesitamos parsearlo
        return JSON.parse(jsonString);
      })
    );
  }
}
