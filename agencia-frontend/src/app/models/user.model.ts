export interface User {
  id: string;
  email: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  sexo?: string;
  isAdmin: boolean;
  isAgente: boolean;
  isCliente: boolean;
  isActive: boolean;
}

export interface AuthPayload {
  token: string;
  type: string;
  usuario: User;
}

export interface LoginInput {
  email: string;
  password: string;
}

export interface RegisterInput {
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  sexo?: string;
}
