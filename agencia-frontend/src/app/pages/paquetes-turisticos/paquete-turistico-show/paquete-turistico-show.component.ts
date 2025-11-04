import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { PaqueteTuristicoService } from '../../../services/paquete-turistico.service';
import { PaqueteTuristico } from '../../../models/paquete-turistico.model';

@Component({
  selector: 'app-paquete-turistico-show',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './paquete-turistico-show.component.html',
  styleUrls: ['./paquete-turistico-show.component.css']
})
export class PaqueteTuristicoShowComponent implements OnInit {
  paquete: PaqueteTuristico | null = null;
  loading: boolean = false;
  error: string = '';

  constructor(
    private paqueteTuristicoService: PaqueteTuristicoService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadPaquete(id);
    }
  }

  loadPaquete(id: string): void {
    this.loading = true;
    this.paqueteTuristicoService.getPaqueteTuristicoById(id).subscribe({
      next: (data) => {
        this.paquete = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar el paquete turístico: ' + err.message;
        this.loading = false;
      }
    });
  }

  editPaquete(): void {
    if (this.paquete?.id) {
      this.router.navigate(['/dashboard/paquetes-turisticos/editar', this.paquete.id]);
    }
  }

  deletePaquete(): void {
    if (!this.paquete?.id) return;

    if (confirm('¿Estás seguro de que deseas eliminar este paquete turístico?')) {
      this.paqueteTuristicoService.deletePaqueteTuristico(this.paquete.id).subscribe({
        next: (success) => {
          if (success) {
            this.router.navigate(['/dashboard/paquetes-turisticos']);
          } else {
            this.error = 'No se pudo eliminar el paquete turístico';
          }
        },
        error: (err) => {
          this.error = 'Error al eliminar el paquete turístico: ' + err.message;
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/dashboard/paquetes-turisticos']);
  }

  getPrecioPorDia(): number {
    if (this.paquete?.precioTotalVenta && this.paquete?.duracionDias && this.paquete.duracionDias > 0) {
      return this.paquete.precioTotalVenta / this.paquete.duracionDias;
    }
    return 0;
  }

  getCostoTotalServicios(): number {
    if (!this.paquete?.servicios) return 0;
    return this.paquete.servicios.reduce((sum, servicio) => {
      return sum + (servicio.precioCosto || 0);
    }, 0);
  }

  getPrecioVentaServicios(): number {
    if (!this.paquete?.servicios) return 0;
    return this.paquete.servicios.reduce((sum, servicio) => {
      return sum + (servicio.precioVenta || 0);
    }, 0);
  }

  getMargenTotal(): number {
    const costoTotal = this.getCostoTotalServicios();
    const ventaTotal = this.getPrecioVentaServicios();
    if (costoTotal > 0) {
      return ((ventaTotal - costoTotal) / costoTotal) * 100;
    }
    return 0;
  }

  getIconoTipoServicio(tipo: string): string {
    const iconos: { [key: string]: string } = {
      'ALOJAMIENTO': '🏨',
      'TRANSPORTE': '✈️',
      'ALIMENTACION': '🍽️',
      'TOUR': '🗺️',
      'ENTRETENIMIENTO': '🎭',
      'SEGURO': '🛡️'
    };
    return iconos[tipo.toUpperCase()] || '📌';
  }
}
