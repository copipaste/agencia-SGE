import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { filter } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;
  sidebarCollapsed = false;
  hasActiveChildRoute = false;

  menuItems = [
    { icon: '📊', label: 'Dashboard', route: '/dashboard', active: true },
    { icon: '👥', label: 'Clientes', route: '/dashboard/clientes', active: false },
    { icon: '🏢', label: 'Proveedores', route: '/dashboard/proveedores', active: false },
    { icon: '🏨', label: 'Servicios', route: '/dashboard/servicios', active: false },
    { icon: '�', label: 'Paquetes Turísticos', route: '/dashboard/paquetes-turisticos', active: false },
    { icon: '💰', label: 'Ventas', route: '/dashboard/ventas', active: false },
    { icon: '👤', label: 'Agentes', route: '/dashboard/agentes', active: false },
    { icon: '⚙️', label: 'Configuración', route: '/dashboard/configuracion', active: false }
  ];

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });

    // Detectar cambios de ruta
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.hasActiveChildRoute = this.router.url !== '/dashboard';
      });

    // Verificar ruta inicial
    this.hasActiveChildRoute = this.router.url !== '/dashboard';
  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  setActiveMenu(index: number): void {
    this.menuItems.forEach((item, i) => {
      item.active = i === index;
    });
  }

  logout(): void {
    if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
      this.authService.logout();
    }
  }

  get userName(): string {
    if (this.currentUser) {
      return `${this.currentUser.nombre} ${this.currentUser.apellido}`;
    }
    return 'Usuario';
  }

  get userRole(): string {
    if (this.currentUser?.isAdmin) return 'Administrador';
    if (this.currentUser?.isAgente) return 'Agente';
    if (this.currentUser?.isCliente) return 'Cliente';
    return 'Usuario';
  }
}
