import { Cliente } from './cliente.model';
import { Agente } from './agente.model';
import { DetalleVenta } from './detalle-venta.model';

export interface Venta {
  id?: string;
  clienteId: string;
  cliente?: Cliente;
  agenteId: string;
  agente?: Agente;
  fechaVenta?: string;
  montoTotal?: number;
  estadoVenta: string; // Pendiente, Confirmada, Cancelada
  metodoPago: string; // Efectivo, Tarjeta, Transferencia
  detalles?: DetalleVenta[];
}
