import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Agente, CreateAgenteInput, UpdateAgenteInput } from '../models/agente.model';

const GET_ALL_AGENTES = gql`
  query GetAllAgentes {
    getAllAgentes {
      id
      usuarioId
      puesto
      fechaContratacion
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
        isActive
      }
    }
  }
`;

const GET_AGENTE_BY_ID = gql`
  query GetAgenteById($id: ID!) {
    getAgenteById(id: $id) {
      id
      usuarioId
      puesto
      fechaContratacion
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
        isActive
      }
    }
  }
`;

const SEARCH_AGENTES = gql`
  query SearchAgentes($searchTerm: String!) {
    searchAgentes(searchTerm: $searchTerm) {
      id
      usuarioId
      puesto
      fechaContratacion
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
      }
    }
  }
`;

const CREATE_AGENTE = gql`
  mutation CreateAgente($input: CreateAgenteInput!) {
    createAgente(input: $input) {
      id
      usuarioId
      puesto
      fechaContratacion
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
        isAgente
        isActive
      }
    }
  }
`;

const UPDATE_AGENTE = gql`
  mutation UpdateAgente($id: ID!, $input: UpdateAgenteInput!) {
    updateAgente(id: $id, input: $input) {
      id
      usuarioId
      puesto
      fechaContratacion
      usuario {
        id
        email
        nombre
        apellido
      }
    }
  }
`;

const DELETE_AGENTE = gql`
  mutation DeleteAgente($id: ID!) {
    deleteAgente(id: $id)
  }
`;

@Injectable({
  providedIn: 'root'
})
export class AgenteService {
  constructor(private apollo: Apollo) {}

  getAllAgentes(): Observable<Agente[]> {
    return this.apollo.query<{ getAllAgentes: Agente[] }>({
      query: GET_ALL_AGENTES,
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.getAllAgentes || [])
    );
  }

  getAgenteById(id: string): Observable<Agente> {
    return this.apollo.query<{ getAgenteById: Agente }>({
      query: GET_AGENTE_BY_ID,
      variables: { id },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data!.getAgenteById)
    );
  }

  searchAgentes(searchTerm: string): Observable<Agente[]> {
    return this.apollo.query<{ searchAgentes: Agente[] }>({
      query: SEARCH_AGENTES,
      variables: { searchTerm },
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.searchAgentes || [])
    );
  }

  createAgente(input: CreateAgenteInput): Observable<Agente> {
    return this.apollo.mutate<{ createAgente: Agente }>({
      mutation: CREATE_AGENTE,
      variables: { input },
      refetchQueries: [{ query: GET_ALL_AGENTES }]
    }).pipe(
      map(result => result.data!.createAgente)
    );
  }

  updateAgente(id: string, input: UpdateAgenteInput): Observable<Agente> {
    return this.apollo.mutate<{ updateAgente: Agente }>({
      mutation: UPDATE_AGENTE,
      variables: { id, input },
      refetchQueries: [{ query: GET_ALL_AGENTES }]
    }).pipe(
      map(result => result.data!.updateAgente)
    );
  }

  deleteAgente(id: string): Observable<boolean> {
    return this.apollo.mutate<{ deleteAgente: boolean }>({
      mutation: DELETE_AGENTE,
      variables: { id },
      refetchQueries: [{ query: GET_ALL_AGENTES }]
    }).pipe(
      map(result => result.data!.deleteAgente)
    );
  }
}
