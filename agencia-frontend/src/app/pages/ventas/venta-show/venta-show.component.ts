import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { VentaService } from '../../../services/venta.service';
import { Venta } from '../../../models/venta.model';

@Component({
  selector: 'app-venta-show',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './venta-show.component.html',
  styleUrl: './venta-show.component.css'
})
export class VentaShowComponent implements OnInit {
  venta: Venta | null = null;
  loading = false;

  constructor(
    private ventaService: VentaService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadVenta(id);
    }
  }

  loadVenta(id: string): void {
    this.loading = true;
    this.ventaService.getVentaById(id).subscribe({
      next: (data) => {
        this.venta = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar venta:', error);
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/ventas']);
  }

  editVenta(): void {
    if (this.venta) {
      this.router.navigate(['/dashboard/ventas/editar', this.venta.id]);
    }
  }

  getClienteNombre(): string {
    return this.venta?.cliente?.usuario ? 
      `${this.venta.cliente.usuario.nombre} ${this.venta.cliente.usuario.apellido}` : 
      'N/A';
  }

  getAgenteNombre(): string {
    return this.venta?.agente?.usuario ? 
      `${this.venta.agente.usuario.nombre} ${this.venta.agente.usuario.apellido}` : 
      'N/A';
  }

  getEstadoBadgeClass(): string {
    if (!this.venta) return 'badge-secondary';
    switch(this.venta.estadoVenta) {
      case 'Confirmada': return 'badge-success';
      case 'Pendiente': return 'badge-warning';
      case 'Cancelada': return 'badge-danger';
      default: return 'badge-secondary';
    }
  }
}
