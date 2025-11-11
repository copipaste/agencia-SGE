import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map, switchMap, tap, catchError, of, delay } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Venta } from '../models/venta.model';
import { DetalleVentaInput } from '../models/detalle-venta.model';

@Injectable({
  providedIn: 'root'
})
export class VentaService {

  private readonly BI_SERVICE_URL = 'http://localhost:8080/api/bi';

  constructor(
    private apollo: Apollo,
    private http: HttpClient
  ) { }

  getAllVentas(): Observable<Venta[]> {
    return this.apollo.query<any>({
      query: gql`
        query GetAllVentas {
          getAllVentas {
            id clienteId agenteId fechaVenta montoTotal estadoVenta metodoPago
            cliente { id usuarioId usuario { nombre apellido } }
            agente { id usuarioId usuario { nombre apellido } }
          }
        }
      `,
      fetchPolicy: 'network-only'
    }).pipe(map(result => result.data.getAllVentas));
  }

  getVentaById(id: string): Observable<Venta> {
    return this.apollo.query<any>({
      query: gql`
        query GetVentaById($id: ID!) {
          getVentaById(id: $id) {
            id clienteId agenteId fechaVenta montoTotal estadoVenta metodoPago
            cliente { id usuario { nombre apellido } }
            agente { id usuario { nombre apellido } }
            detalles {
              id servicioId paqueteId cantidad precioUnitarioVenta subtotal
              servicio { id nombreServicio precioVenta }
              paquete { id nombrePaquete precioTotalVenta }
            }
          }
        }
      `,
      variables: { id },
      fetchPolicy: 'network-only'
    }).pipe(map(result => result.data.getVentaById));
  }

  createVenta(venta: Venta, detalles: DetalleVentaInput[]): Observable<Venta> {
    return this.apollo.mutate<any>({
      mutation: gql`
        mutation CreateVenta($input: CreateVentaInput!) {
          createVenta(input: $input) {
            id clienteId agenteId fechaVenta montoTotal estadoVenta metodoPago
          }
        }
      `,
      variables: {
        input: {
          clienteId: venta.clienteId,
          agenteId: venta.agenteId,
          estadoVenta: venta.estadoVenta,
          metodoPago: venta.metodoPago,
          detalles: detalles
        }
      }
    }).pipe(map(result => result.data.createVenta));
  }

  updateVenta(id: string, venta: Partial<Venta>, detalles?: DetalleVentaInput[]): Observable<Venta> {
    const input: any = {};
    if (venta.estadoVenta !== undefined) input.estadoVenta = venta.estadoVenta;
    if (venta.metodoPago !== undefined) input.metodoPago = venta.metodoPago;
    if (detalles !== undefined) input.detalles = detalles;

    return this.apollo.mutate<any>({
      mutation: gql`
        mutation UpdateVenta($id: ID!, $input: UpdateVentaInput!) {
          updateVenta(id: $id, input: $input) {
            id estadoVenta metodoPago montoTotal
          }
        }
      `,
      variables: { id, input }
    }).pipe(
      map(result => result.data.updateVenta),
      // Sincronizar con BI automáticamente después de actualizar
      tap(venta => {
        console.log('✅ Venta actualizada, sincronizando con BI...');
        this.sincronizarBI().subscribe({
          next: () => console.log('✅ Sincronización BI completada'),
          error: (err) => console.warn('⚠️ Error al sincronizar BI (no crítico):', err)
        });
      })
    );
  }

  /**
   * Fuerza la sincronización inmediata con el servicio BI
   * Se ejecuta automáticamente después de actualizar ventas
   */
  private sincronizarBI(): Observable<any> {
    // Llamar al endpoint /sync/force a través del backend Spring Boot
    return this.http.post(`${this.BI_SERVICE_URL}/sync/force`, {}).pipe(
      catchError(error => {
        console.warn('Error al sincronizar BI:', error);
        return of(null); // No fallar si la sincronización falla
      })
    );
  }

  deleteVenta(id: string): Observable<boolean> {
    return this.apollo.mutate<any>({
      mutation: gql`
        mutation DeleteVenta($id: ID!) {
          deleteVenta(id: $id)
        }
      `,
      variables: { id }
    }).pipe(map(result => result.data.deleteVenta));
  }

  notificarVenta(ventaId: string): Observable<{ success: boolean; message: string }> {
    return this.apollo.mutate<any>({
      mutation: gql`
        mutation NotificarVenta($ventaId: ID!) {
          notificarVenta(ventaId: $ventaId) {
            success
            message
          }
        }
      `,
      variables: { ventaId }
    }).pipe(map(result => result.data.notificarVenta));
  }
}
