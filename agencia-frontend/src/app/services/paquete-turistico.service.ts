import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { PaqueteTuristico } from '../models/paquete-turistico.model';

@Injectable({
  providedIn: 'root'
})
export class PaqueteTuristicoService {

  constructor(private apollo: Apollo) { }

  // Query para obtener todos los paquetes turísticos
  getAllPaquetesTuristicos(): Observable<PaqueteTuristico[]> {
    return this.apollo.query<any>({
      query: gql`
        query GetAllPaquetesTuristicos {
          getAllPaquetesTuristicos {
            id
            nombrePaquete
            descripcion
            destinoPrincipal
            duracionDias
            precioTotalVenta
            servicios {
              id
              nombreServicio
              tipoServicio
              destinoCiudad
              precioVenta
            }
          }
        }
      `,
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data.getAllPaquetesTuristicos)
    );
  }

  // Query para obtener un paquete turístico por ID
  getPaqueteTuristicoById(id: string): Observable<PaqueteTuristico> {
    return this.apollo.query<any>({
      query: gql`
        query GetPaqueteTuristicoById($id: ID!) {
          getPaqueteTuristicoById(id: $id) {
            id
            nombrePaquete
            descripcion
            destinoPrincipal
            duracionDias
            precioTotalVenta
            servicios {
              id
              nombreServicio
              tipoServicio
              descripcion
              destinoCiudad
              destinoPais
              precioCosto
              precioVenta
              isAvailable
              proveedor {
                id
                nombreEmpresa
              }
            }
          }
        }
      `,
      variables: { id },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data.getPaqueteTuristicoById)
    );
  }

  // Query para obtener paquetes por destino
  getPaquetesTuristicosByDestino(destino: string): Observable<PaqueteTuristico[]> {
    return this.apollo.query<any>({
      query: gql`
        query GetPaquetesTuristicosByDestino($destino: String!) {
          getPaquetesTuristicosByDestino(destino: $destino) {
            id
            nombrePaquete
            descripcion
            destinoPrincipal
            duracionDias
            precioTotalVenta
            servicios {
              id
              nombreServicio
              tipoServicio
            }
          }
        }
      `,
      variables: { destino },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data.getPaquetesTuristicosByDestino)
    );
  }

  // Query para buscar paquetes turísticos
  searchPaquetesTuristicos(keyword: string): Observable<PaqueteTuristico[]> {
    return this.apollo.query<any>({
      query: gql`
        query SearchPaquetesTuristicos($keyword: String!) {
          searchPaquetesTuristicos(keyword: $keyword) {
            id
            nombrePaquete
            descripcion
            destinoPrincipal
            duracionDias
            precioTotalVenta
            servicios {
              id
              nombreServicio
            }
          }
        }
      `,
      variables: { keyword },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data.searchPaquetesTuristicos)
    );
  }

  // Mutation para crear un paquete turístico
  createPaqueteTuristico(paquete: PaqueteTuristico, serviciosIds: string[]): Observable<PaqueteTuristico> {
    return this.apollo.mutate<any>({
      mutation: gql`
        mutation CreatePaqueteTuristico($input: CreatePaqueteTuristicoInput!) {
          createPaqueteTuristico(input: $input) {
            id
            nombrePaquete
            descripcion
            destinoPrincipal
            duracionDias
            precioTotalVenta
          }
        }
      `,
      variables: {
        input: {
          nombrePaquete: paquete.nombrePaquete,
          descripcion: paquete.descripcion || null,
          destinoPrincipal: paquete.destinoPrincipal,
          duracionDias: paquete.duracionDias || null,
          precioTotalVenta: paquete.precioTotalVenta || null,
          serviciosIds: serviciosIds
        }
      }
    }).pipe(
      map(result => result.data.createPaqueteTuristico)
    );
  }

  // Mutation para actualizar un paquete turístico
  updatePaqueteTuristico(id: string, paquete: Partial<PaqueteTuristico>, serviciosIds?: string[]): Observable<PaqueteTuristico> {
    // Construir el input filtrando valores undefined
    const input: any = {};
    
    if (paquete.nombrePaquete !== undefined) input.nombrePaquete = paquete.nombrePaquete;
    if (paquete.descripcion !== undefined) input.descripcion = paquete.descripcion;
    if (paquete.destinoPrincipal !== undefined) input.destinoPrincipal = paquete.destinoPrincipal;
    if (paquete.duracionDias !== undefined) input.duracionDias = paquete.duracionDias;
    if (paquete.precioTotalVenta !== undefined) input.precioTotalVenta = paquete.precioTotalVenta;
    if (serviciosIds !== undefined) input.serviciosIds = serviciosIds;

    return this.apollo.mutate<any>({
      mutation: gql`
        mutation UpdatePaqueteTuristico($id: ID!, $input: UpdatePaqueteTuristicoInput!) {
          updatePaqueteTuristico(id: $id, input: $input) {
            id
            nombrePaquete
            descripcion
            destinoPrincipal
            duracionDias
            precioTotalVenta
          }
        }
      `,
      variables: {
        id,
        input
      }
    }).pipe(
      map(result => result.data.updatePaqueteTuristico)
    );
  }

  // Mutation para eliminar un paquete turístico
  deletePaqueteTuristico(id: string): Observable<boolean> {
    return this.apollo.mutate<any>({
      mutation: gql`
        mutation DeletePaqueteTuristico($id: ID!) {
          deletePaqueteTuristico(id: $id)
        }
      `,
      variables: { id }
    }).pipe(
      map(result => result.data.deletePaqueteTuristico)
    );
  }

  // Mutation para agregar servicio a un paquete
  addServicioToPaquete(paqueteId: string, servicioId: string): Observable<boolean> {
    return this.apollo.mutate<any>({
      mutation: gql`
        mutation AddServicioToPaquete($paqueteId: ID!, $servicioId: ID!) {
          addServicioToPaquete(paqueteId: $paqueteId, servicioId: $servicioId)
        }
      `,
      variables: { paqueteId, servicioId }
    }).pipe(
      map(result => result.data.addServicioToPaquete)
    );
  }

  // Mutation para eliminar servicio de un paquete
  removeServicioFromPaquete(paqueteId: string, servicioId: string): Observable<boolean> {
    return this.apollo.mutate<any>({
      mutation: gql`
        mutation RemoveServicioFromPaquete($paqueteId: ID!, $servicioId: ID!) {
          removeServicioFromPaquete(paqueteId: $paqueteId, servicioId: $servicioId)
        }
      `,
      variables: { paqueteId, servicioId }
    }).pipe(
      map(result => result.data.removeServicioFromPaquete)
    );
  }
}
