import { Servicio } from './servicio.model';
import { PaqueteTuristico } from './paquete-turistico.model';

export interface DetalleVenta {
  id?: string;
  ventaId: string;
  servicioId?: string | null;
  servicio?: Servicio;
  paqueteId?: string | null;
  paquete?: PaqueteTuristico;
  cantidad: number;
  precioUnitarioVenta: number;
  subtotal?: number;
}

// Interface para crear/editar detalles
export interface DetalleVentaInput {
  servicioId?: string | null;
  paqueteId?: string | null;
  cantidad: number;
  precioUnitarioVenta: number;
}
