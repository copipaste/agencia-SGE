import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map } from 'rxjs';

export interface ReporteVentas {
  periodo: string;
  fechaInicio: string;
  fechaFin: string;
  totalVentas: number;
  montoTotal: number;
  promedioVenta: number;
  ventasPorEstado: VentaPorEstado[];
  ventasPorMetodoPago: VentaPorMetodoPago[];
  topAgentes: TopAgente[];
}

export interface VentaPorEstado {
  estado: string;
  cantidad: number;
  monto: number;
}

export interface VentaPorMetodoPago {
  metodoPago: string;
  cantidad: number;
  monto: number;
}

export interface TopAgente {
  agenteId: string;
  agenteNombre: string;
  cantidadVentas: number;
  montoTotal: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteVentasService {

  constructor(private apollo: Apollo) { }

  getReporteVentas(periodo: 'SEMANAL' | 'MENSUAL' | 'ANUAL'): Observable<ReporteVentas> {
    return this.apollo.query<any>({
      query: gql`
        query GetReporteVentas($periodo: String!) {
          getReporteVentas(periodo: $periodo) {
            periodo
            fechaInicio
            fechaFin
            totalVentas
            montoTotal
            promedioVenta
            ventasPorEstado {
              estado
              cantidad
              monto
            }
            ventasPorMetodoPago {
              metodoPago
              cantidad
              monto
            }
            topAgentes {
              agenteId
              agenteNombre
              cantidadVentas
              montoTotal
            }
          }
        }
      `,
      variables: { periodo },
      fetchPolicy: 'network-only'
    }).pipe(map(result => result.data.getReporteVentas));
  }
}
