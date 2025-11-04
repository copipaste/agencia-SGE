import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { Servicio, CreateServicioInput, UpdateServicioInput } from '../models/servicio.model';

const GET_ALL_SERVICIOS = gql`
  query GetAllServicios {
    getAllServicios {
      id
      proveedorId
      proveedor {
        id
        nombreEmpresa
        tipoServicio
      }
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      destinoPais
      precioCosto
      precioVenta
      isAvailable
    }
  }
`;

const GET_SERVICIO_BY_ID = gql`
  query GetServicioById($id: ID!) {
    getServicioById(id: $id) {
      id
      proveedorId
      proveedor {
        id
        nombreEmpresa
        tipoServicio
      }
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      destinoPais
      precioCosto
      precioVenta
      isAvailable
    }
  }
`;

const GET_SERVICIOS_BY_PROVEEDOR = gql`
  query GetServiciosByProveedorId($proveedorId: ID!) {
    getServiciosByProveedorId(proveedorId: $proveedorId) {
      id
      proveedorId
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      destinoPais
      precioCosto
      precioVenta
      isAvailable
    }
  }
`;

const GET_SERVICIOS_BY_TIPO = gql`
  query GetServiciosByTipo($tipoServicio: String!) {
    getServiciosByTipo(tipoServicio: $tipoServicio) {
      id
      proveedorId
      proveedor {
        id
        nombreEmpresa
      }
      tipoServicio
      nombreServicio
      destinoCiudad
      destinoPais
      precioVenta
      isAvailable
    }
  }
`;

const SEARCH_SERVICIOS = gql`
  query SearchServicios($searchTerm: String!) {
    searchServicios(searchTerm: $searchTerm) {
      id
      proveedorId
      proveedor {
        id
        nombreEmpresa
      }
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      destinoPais
      precioVenta
      isAvailable
    }
  }
`;

const CREATE_SERVICIO = gql`
  mutation CreateServicio($input: CreateServicioInput!) {
    createServicio(input: $input) {
      id
      proveedorId
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      destinoPais
      precioCosto
      precioVenta
      isAvailable
    }
  }
`;

const UPDATE_SERVICIO = gql`
  mutation UpdateServicio($id: ID!, $input: UpdateServicioInput!) {
    updateServicio(id: $id, input: $input) {
      id
      proveedorId
      tipoServicio
      nombreServicio
      descripcion
      destinoCiudad
      destinoPais
      precioCosto
      precioVenta
      isAvailable
    }
  }
`;

const DELETE_SERVICIO = gql`
  mutation DeleteServicio($id: ID!) {
    deleteServicio(id: $id)
  }
`;

@Injectable({
  providedIn: 'root'
})
export class ServicioService {

  constructor(private apollo: Apollo) {}

  getAllServicios(): Observable<Servicio[]> {
    return this.apollo.query<{ getAllServicios: Servicio[] }>({
      query: GET_ALL_SERVICIOS,
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getAllServicios || [])
    );
  }

  getServicioById(id: string): Observable<Servicio> {
    return this.apollo.query<{ getServicioById: Servicio }>({
      query: GET_SERVICIO_BY_ID,
      variables: { id },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data!.getServicioById)
    );
  }

  getServiciosByProveedorId(proveedorId: string): Observable<Servicio[]> {
    return this.apollo.query<{ getServiciosByProveedorId: Servicio[] }>({
      query: GET_SERVICIOS_BY_PROVEEDOR,
      variables: { proveedorId },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getServiciosByProveedorId || [])
    );
  }

  getServiciosByTipo(tipoServicio: string): Observable<Servicio[]> {
    return this.apollo.query<{ getServiciosByTipo: Servicio[] }>({
      query: GET_SERVICIOS_BY_TIPO,
      variables: { tipoServicio },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getServiciosByTipo || [])
    );
  }

  searchServicios(searchTerm: string): Observable<Servicio[]> {
    return this.apollo.query<{ searchServicios: Servicio[] }>({
      query: SEARCH_SERVICIOS,
      variables: { searchTerm },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.searchServicios || [])
    );
  }

  createServicio(input: CreateServicioInput): Observable<Servicio> {
    return this.apollo.mutate<{ createServicio: Servicio }>({
      mutation: CREATE_SERVICIO,
      variables: { input },
      refetchQueries: [{ query: GET_ALL_SERVICIOS }]
    }).pipe(
      map(result => result.data!.createServicio)
    );
  }

  updateServicio(id: string, input: UpdateServicioInput): Observable<Servicio> {
    return this.apollo.mutate<{ updateServicio: Servicio }>({
      mutation: UPDATE_SERVICIO,
      variables: { id, input },
      refetchQueries: [{ query: GET_ALL_SERVICIOS }]
    }).pipe(
      map(result => result.data!.updateServicio)
    );
  }

  deleteServicio(id: string): Observable<boolean> {
    return this.apollo.mutate<{ deleteServicio: boolean }>({
      mutation: DELETE_SERVICIO,
      variables: { id },
      refetchQueries: [{ query: GET_ALL_SERVICIOS }]
    }).pipe(
      map(result => result.data!.deleteServicio)
    );
  }
}
