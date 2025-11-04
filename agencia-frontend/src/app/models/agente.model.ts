import { User } from './user.model';

export interface Agente {
  id: string;
  usuarioId: string;
  usuario?: User;
  puesto: string;
  fechaContratacion?: string;
}

export interface CreateAgenteInput {
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  sexo?: string;
  puesto: string;
  fechaContratacion?: string;
}

export interface UpdateAgenteInput {
  puesto?: string;
  fechaContratacion?: string;
}
