import { Servicio } from './servicio.model';

export interface PaqueteTuristico {
  id?: string;
  nombrePaquete: string;
  descripcion?: string;
  destinoPrincipal: string;
  duracionDias?: number;
  precioTotalVenta?: number;
  servicios?: Servicio[];
}
