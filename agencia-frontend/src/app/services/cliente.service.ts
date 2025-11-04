import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import { Cliente, CreateClienteInput, UpdateClienteInput } from '../models/cliente.model';

const GET_ALL_CLIENTES = gql`
  query GetAllClientes {
    getAllClientes {
      id
      usuarioId
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
      }
      direccion
      fechaNacimiento
      numeroPasaporte
    }
  }
`;

const GET_CLIENTE_BY_ID = gql`
  query GetClienteById($id: ID!) {
    getClienteById(id: $id) {
      id
      usuarioId
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
      }
      direccion
      fechaNacimiento
      numeroPasaporte
    }
  }
`;

const SEARCH_CLIENTES = gql`
  query SearchClientes($searchTerm: String!) {
    searchClientes(searchTerm: $searchTerm) {
      id
      usuarioId
      usuario {
        id
        email
        nombre
        apellido
        telefono
      }
      direccion
      fechaNacimiento
      numeroPasaporte
    }
  }
`;

const CREATE_CLIENTE = gql`
  mutation CreateCliente($input: CreateClienteInput!) {
    createCliente(input: $input) {
      id
      usuarioId
      usuario {
        id
        email
        nombre
        apellido
      }
      direccion
      fechaNacimiento
      numeroPasaporte
    }
  }
`;

const UPDATE_CLIENTE = gql`
  mutation UpdateCliente($id: ID!, $input: UpdateClienteInput!) {
    updateCliente(id: $id, input: $input) {
      id
      usuarioId
      usuario {
        id
        email
        nombre
        apellido
      }
      direccion
      fechaNacimiento
      numeroPasaporte
    }
  }
`;

const DELETE_CLIENTE = gql`
  mutation DeleteCliente($id: ID!) {
    deleteCliente(id: $id)
  }
`;

const TOGGLE_CLIENTE_STATUS = gql`
  mutation ToggleClienteStatus($id: ID!) {
    toggleClienteStatus(id: $id) {
      id
      usuarioId
    }
  }
`;

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  constructor(private apollo: Apollo) {}

  getAllClientes(): Observable<Cliente[]> {
    return this.apollo.query<{ getAllClientes: Cliente[] }>({
      query: GET_ALL_CLIENTES,
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getAllClientes || [])
    );
  }

  getClienteById(id: string): Observable<Cliente> {
    return this.apollo.query<{ getClienteById: Cliente }>({
      query: GET_CLIENTE_BY_ID,
      variables: { id },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data!.getClienteById)
    );
  }

  searchClientes(searchTerm: string): Observable<Cliente[]> {
    return this.apollo.query<{ searchClientes: Cliente[] }>({
      query: SEARCH_CLIENTES,
      variables: { searchTerm },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.searchClientes || [])
    );
  }

  createCliente(input: CreateClienteInput): Observable<Cliente> {
    return this.apollo.mutate<{ createCliente: Cliente }>({
      mutation: CREATE_CLIENTE,
      variables: { input },
      refetchQueries: [{ query: GET_ALL_CLIENTES }]
    }).pipe(
      map(result => result.data!.createCliente)
    );
  }

  updateCliente(id: string, input: UpdateClienteInput): Observable<Cliente> {
    return this.apollo.mutate<{ updateCliente: Cliente }>({
      mutation: UPDATE_CLIENTE,
      variables: { id, input },
      refetchQueries: [{ query: GET_ALL_CLIENTES }]
    }).pipe(
      map(result => result.data!.updateCliente)
    );
  }

  deleteCliente(id: string): Observable<boolean> {
    return this.apollo.mutate<{ deleteCliente: boolean }>({
      mutation: DELETE_CLIENTE,
      variables: { id },
      refetchQueries: [{ query: GET_ALL_CLIENTES }]
    }).pipe(
      map(result => result.data!.deleteCliente)
    );
  }

  toggleClienteStatus(id: string): Observable<Cliente> {
    return this.apollo.mutate<{ toggleClienteStatus: Cliente }>({
      mutation: TOGGLE_CLIENTE_STATUS,
      variables: { id },
      refetchQueries: [{ query: GET_ALL_CLIENTES }]
    }).pipe(
      map(result => result.data!.toggleClienteStatus)
    );
  }
}
