export interface Proveedor {
  id: string;
  nombreEmpresa: string;
  tipoServicio: string;
  contactoNombre?: string;
  contactoEmail?: string;
  contactoTelefono?: string;
}

export interface CreateProveedorInput {
  nombreEmpresa: string;
  tipoServicio: string;
  contactoNombre?: string;
  contactoEmail?: string;
  contactoTelefono?: string;
}

export interface UpdateProveedorInput {
  nombreEmpresa?: string;
  tipoServicio?: string;
  contactoNombre?: string;
  contactoEmail?: string;
  contactoTelefono?: string;
}
