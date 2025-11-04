import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { Proveedor, CreateProveedorInput, UpdateProveedorInput } from '../models/proveedor.model';

const GET_ALL_PROVEEDORES = gql`
  query GetAllProveedores {
    getAllProveedores {
      id
      nombreEmpresa
      tipoServicio
      contactoNombre
      contactoEmail
      contactoTelefono
    }
  }
`;

const GET_PROVEEDOR_BY_ID = gql`
  query GetProveedorById($id: ID!) {
    getProveedorById(id: $id) {
      id
      nombreEmpresa
      tipoServicio
      contactoNombre
      contactoEmail
      contactoTelefono
    }
  }
`;

const GET_PROVEEDORES_BY_TIPO_SERVICIO = gql`
  query GetProveedoresByTipoServicio($tipoServicio: String!) {
    getProveedoresByTipoServicio(tipoServicio: $tipoServicio) {
      id
      nombreEmpresa
      tipoServicio
      contactoNombre
      contactoEmail
      contactoTelefono
    }
  }
`;

const SEARCH_PROVEEDORES = gql`
  query SearchProveedores($searchTerm: String!) {
    searchProveedores(searchTerm: $searchTerm) {
      id
      nombreEmpresa
      tipoServicio
      contactoNombre
      contactoEmail
      contactoTelefono
    }
  }
`;

const CREATE_PROVEEDOR = gql`
  mutation CreateProveedor($input: CreateProveedorInput!) {
    createProveedor(input: $input) {
      id
      nombreEmpresa
      tipoServicio
      contactoNombre
      contactoEmail
      contactoTelefono
    }
  }
`;

const UPDATE_PROVEEDOR = gql`
  mutation UpdateProveedor($id: ID!, $input: UpdateProveedorInput!) {
    updateProveedor(id: $id, input: $input) {
      id
      nombreEmpresa
      tipoServicio
      contactoNombre
      contactoEmail
      contactoTelefono
    }
  }
`;

const DELETE_PROVEEDOR = gql`
  mutation DeleteProveedor($id: ID!) {
    deleteProveedor(id: $id)
  }
`;

@Injectable({
  providedIn: 'root'
})
export class ProveedorService {

  constructor(private apollo: Apollo) {}

  getAllProveedores(): Observable<Proveedor[]> {
    return this.apollo.query<{ getAllProveedores: Proveedor[] }>({
      query: GET_ALL_PROVEEDORES,
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getAllProveedores || [])
    );
  }

  getProveedorById(id: string): Observable<Proveedor> {
    return this.apollo.query<{ getProveedorById: Proveedor }>({
      query: GET_PROVEEDOR_BY_ID,
      variables: { id },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data!.getProveedorById)
    );
  }

  getProveedoresByTipoServicio(tipoServicio: string): Observable<Proveedor[]> {
    return this.apollo.query<{ getProveedoresByTipoServicio: Proveedor[] }>({
      query: GET_PROVEEDORES_BY_TIPO_SERVICIO,
      variables: { tipoServicio },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getProveedoresByTipoServicio || [])
    );
  }

  searchProveedores(searchTerm: string): Observable<Proveedor[]> {
    return this.apollo.query<{ searchProveedores: Proveedor[] }>({
      query: SEARCH_PROVEEDORES,
      variables: { searchTerm },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.searchProveedores || [])
    );
  }

  createProveedor(input: CreateProveedorInput): Observable<Proveedor> {
    return this.apollo.mutate<{ createProveedor: Proveedor }>({
      mutation: CREATE_PROVEEDOR,
      variables: { input },
      refetchQueries: [{ query: GET_ALL_PROVEEDORES }]
    }).pipe(
      map(result => result.data!.createProveedor)
    );
  }

  updateProveedor(id: string, input: UpdateProveedorInput): Observable<Proveedor> {
    return this.apollo.mutate<{ updateProveedor: Proveedor }>({
      mutation: UPDATE_PROVEEDOR,
      variables: { id, input },
      refetchQueries: [{ query: GET_ALL_PROVEEDORES }]
    }).pipe(
      map(result => result.data!.updateProveedor)
    );
  }

  deleteProveedor(id: string): Observable<boolean> {
    return this.apollo.mutate<{ deleteProveedor: boolean }>({
      mutation: DELETE_PROVEEDOR,
      variables: { id },
      refetchQueries: [{ query: GET_ALL_PROVEEDORES }]
    }).pipe(
      map(result => result.data!.deleteProveedor)
    );
  }
}
