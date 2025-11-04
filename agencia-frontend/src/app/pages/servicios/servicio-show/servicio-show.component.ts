import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ServicioService } from '../../../services/servicio.service';
import { Servicio } from '../../../models/servicio.model';

@Component({
  selector: 'app-servicio-show',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './servicio-show.component.html',
  styleUrl: './servicio-show.component.css'
})
export class ServicioShowComponent implements OnInit {
  servicio: Servicio | null = null;
  loading = true;
  errorMessage = '';
  servicioId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private servicioService: ServicioService
  ) {}

  ngOnInit(): void {
    this.servicioId = this.route.snapshot.paramMap.get('id');
    
    if (this.servicioId) {
      this.loadServicio();
    } else {
      this.errorMessage = 'ID de servicio no proporcionado';
      this.loading = false;
    }
  }

  loadServicio(): void {
    if (!this.servicioId) return;

    this.loading = true;
    this.servicioService.getServicioById(this.servicioId).subscribe({
      next: (servicio) => {
        this.servicio = servicio;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar servicio:', error);
        this.errorMessage = 'Error al cargar los datos del servicio';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/servicios']);
  }

  editServicio(): void {
    if (this.servicioId) {
      this.router.navigate(['/dashboard/servicios/editar', this.servicioId]);
    }
  }

  deleteServicio(): void {
    if (!this.servicioId) return;

    if (confirm('¿Estás seguro de que deseas eliminar este servicio?')) {
      this.servicioService.deleteServicio(this.servicioId).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/servicios']);
        },
        error: (error) => {
          console.error('Error al eliminar servicio:', error);
          this.errorMessage = 'Error al eliminar el servicio';
        }
      });
    }
  }

  getIconByTipo(tipo: string): string {
    const icons: { [key: string]: string } = {
      'Vuelo': '✈️',
      'Hotel': '🏨',
      'Tour': '🗺️',
      'Transporte': '🚌',
      'Restaurante': '🍽️'
    };
    return icons[tipo] || '📦';
  }

  calculateMargin(): number | null {
    if (this.servicio?.precioVenta && this.servicio?.precioCosto) {
      return this.servicio.precioVenta - this.servicio.precioCosto;
    }
    return null;
  }

  calculateMarginPercentage(): number | null {
    if (this.servicio?.precioVenta && this.servicio?.precioCosto && this.servicio.precioCosto > 0) {
      return ((this.servicio.precioVenta - this.servicio.precioCosto) / this.servicio.precioCosto) * 100;
    }
    return null;
  }
}
