import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DashboardBiComponent } from './pages/dashboard-bi/dashboard-bi.component';
import { ClienteListComponent } from './pages/clientes/cliente-list/cliente-list.component';
import { ClienteFormComponent } from './pages/clientes/cliente-form/cliente-form.component';
import { ClienteShowComponent } from './pages/clientes/cliente-show/cliente-show.component';
import { AgenteListComponent } from './pages/agentes/agente-list/agente-list.component';
import { AgenteFormComponent } from './pages/agentes/agente-form/agente-form.component';
import { AgenteShowComponent } from './pages/agentes/agente-show/agente-show.component';
import { ProveedorListComponent } from './pages/proveedores/proveedor-list/proveedor-list.component';
import { ProveedorFormComponent } from './pages/proveedores/proveedor-form/proveedor-form.component';
import { ProveedorShowComponent } from './pages/proveedores/proveedor-show/proveedor-show.component';
import { ServicioListComponent } from './pages/servicios/servicio-list/servicio-list.component';
import { ServicioFormComponent } from './pages/servicios/servicio-form/servicio-form.component';
import { ServicioShowComponent } from './pages/servicios/servicio-show/servicio-show.component';
import { PaqueteTuristicoListComponent } from './pages/paquetes-turisticos/paquete-turistico-list/paquete-turistico-list.component';
import { PaqueteTuristicoFormComponent } from './pages/paquetes-turisticos/paquete-turistico-form/paquete-turistico-form.component';
import { PaqueteTuristicoShowComponent } from './pages/paquetes-turisticos/paquete-turistico-show/paquete-turistico-show.component';
import { VentaListComponent } from './pages/ventas/venta-list/venta-list.component';
import { VentaFormComponent } from './pages/ventas/venta-form/venta-form.component';
import { VentaShowComponent } from './pages/ventas/venta-show/venta-show.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [authGuard],
    children: [
      { path: 'clientes', component: ClienteListComponent },
      { path: 'clientes/nuevo', component: ClienteFormComponent },
      { path: 'clientes/ver/:id', component: ClienteShowComponent },
      { path: 'clientes/editar/:id', component: ClienteFormComponent },
      { path: 'agentes', component: AgenteListComponent },
      { path: 'agentes/nuevo', component: AgenteFormComponent },
      { path: 'agentes/ver/:id', component: AgenteShowComponent },
      { path: 'agentes/editar/:id', component: AgenteFormComponent },
      { path: 'proveedores', component: ProveedorListComponent },
      { path: 'proveedores/nuevo', component: ProveedorFormComponent },
      { path: 'proveedores/ver/:id', component: ProveedorShowComponent },
      { path: 'proveedores/editar/:id', component: ProveedorFormComponent },
      { path: 'servicios', component: ServicioListComponent },
      { path: 'servicios/nuevo', component: ServicioFormComponent },
      { path: 'servicios/ver/:id', component: ServicioShowComponent },
      { path: 'servicios/editar/:id', component: ServicioFormComponent },
      { path: 'paquetes-turisticos', component: PaqueteTuristicoListComponent },
      { path: 'paquetes-turisticos/nuevo', component: PaqueteTuristicoFormComponent },
      { path: 'paquetes-turisticos/ver/:id', component: PaqueteTuristicoShowComponent },
      { path: 'paquetes-turisticos/editar/:id', component: PaqueteTuristicoFormComponent },
      { path: 'ventas', component: VentaListComponent },
      { path: 'ventas/nuevo', component: VentaFormComponent },
      { path: 'ventas/ver/:id', component: VentaShowComponent },
      { path: 'ventas/editar/:id', component: VentaFormComponent },
      { path: 'business-intelligence', component: DashboardBiComponent }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
