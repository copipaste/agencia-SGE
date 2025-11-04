export interface Cliente {
  id: string;
  usuarioId: string;
  usuario?: {
    id: string;
    email: string;
    nombre: string;
    apellido: string;
    telefono?: string;
    sexo?: string;
  };
  direccion: string;
  fechaNacimiento?: string;
  numeroPasaporte: string;
}

export interface CreateClienteInput {
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  sexo?: string;
  direccion: string;
  fechaNacimiento?: string;
  numeroPasaporte: string;
}

export interface UpdateClienteInput {
  direccion?: string;
  fechaNacimiento?: string;
  numeroPasaporte?: string;
}
