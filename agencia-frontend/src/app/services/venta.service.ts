import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { Venta } from '../models/venta.model';
import { DetalleVentaInput } from '../models/detalle-venta.model';

@Injectable({
  providedIn: 'root'
})
export class VentaService {

  constructor(private apollo: Apollo) { }

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
    }).pipe(map(result => result.data.updateVenta));
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
