import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { AuthPayload, LoginInput, RegisterInput, User } from '../models/user.model';
import { Router } from '@angular/router';

const LOGIN_MUTATION = gql`
  mutation Login($input: LoginInput!) {
    login(input: $input) {
      token
      type
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
        isAdmin
        isAgente
        isCliente
        isActive
      }
    }
  }
`;

const REGISTER_MUTATION = gql`
  mutation Register($input: RegisterInput!) {
    register(input: $input) {
      token
      type
      usuario {
        id
        email
        nombre
        apellido
        telefono
        sexo
        isAdmin
        isAgente
        isCliente
        isActive
      }
    }
  }
`;

const ME_QUERY = gql`
  query Me {
    me {
      id
      email
      nombre
      apellido
      telefono
      sexo
      isAdmin
      isAgente
      isCliente
      isActive
    }
  }
`;

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private apollo: Apollo, private router: Router) {
    this.loadUserFromToken();
  }

  private loadUserFromToken(): void {
    const token = this.getToken();
    if (token) {
      this.getCurrentUser().subscribe({
        next: (user) => this.currentUserSubject.next(user),
        error: () => this.logout()
      });
    }
  }

  login(input: LoginInput): Observable<AuthPayload> {
    return this.apollo.mutate<{ login: AuthPayload }>({
      mutation: LOGIN_MUTATION,
      variables: { input }
    }).pipe(
      map(result => result.data!.login),
      tap(authPayload => {
        this.setToken(authPayload.token);
        this.currentUserSubject.next(authPayload.usuario);
      })
    );
  }

  register(input: RegisterInput): Observable<AuthPayload> {
    return this.apollo.mutate<{ register: AuthPayload }>({
      mutation: REGISTER_MUTATION,
      variables: { input }
    }).pipe(
      map(result => result.data!.register),
      tap(authPayload => {
        this.setToken(authPayload.token);
        this.currentUserSubject.next(authPayload.usuario);
      })
    );
  }

  getCurrentUser(): Observable<User> {
    return this.apollo.query<{ me: User }>({
      query: ME_QUERY,
      fetchPolicy: 'network-only'
    }).pipe(
      map(result => result.data?.me as User)
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
    this.apollo.client.clearStore();
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  private setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }
}
