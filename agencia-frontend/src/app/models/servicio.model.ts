export interface Servicio {
  id: string;
  proveedorId?: string;
  proveedor?: {
    id: string;
    nombreEmpresa: string;
    tipoServicio: string;
  };
  tipoServicio: string;
  nombreServicio: string;
  descripcion?: string;
  destinoCiudad?: string;
  destinoPais?: string;
  precioCosto?: number;
  precioVenta?: number;
  isAvailable?: boolean;
}

export interface CreateServicioInput {
  proveedorId?: string;
  tipoServicio: string;
  nombreServicio: string;
  descripcion?: string;
  destinoCiudad?: string;
  destinoPais?: string;
  precioCosto?: number;
  precioVenta?: number;
  isAvailable?: boolean;
}

export interface UpdateServicioInput {
  proveedorId?: string;
  tipoServicio?: string;
  nombreServicio?: string;
  descripcion?: string;
  destinoCiudad?: string;
  destinoPais?: string;
  precioCosto?: number;
  precioVenta?: number;
  isAvailable?: boolean;
}
