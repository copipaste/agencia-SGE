import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PaqueteTuristicoService } from '../../../services/paquete-turistico.service';
import { ServicioService } from '../../../services/servicio.service';
import { PaqueteTuristico } from '../../../models/paquete-turistico.model';
import { Servicio } from '../../../models/servicio.model';

@Component({
  selector: 'app-paquete-turistico-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './paquete-turistico-form.component.html',
  styleUrls: ['./paquete-turistico-form.component.css']
})
export class PaqueteTuristicoFormComponent implements OnInit {
  paquete: PaqueteTuristico = {
    nombrePaquete: '',
    descripcion: '',
    destinoPrincipal: '',
    duracionDias: undefined,
    precioTotalVenta: undefined
  };

  serviciosDisponibles: Servicio[] = [];
  serviciosSeleccionados: string[] = [];
  
  isEditMode: boolean = false;
  paqueteId: string | null = null;
  loading: boolean = false;
  error: string = '';
  searchServicio: string = '';

  constructor(
    private paqueteTuristicoService: PaqueteTuristicoService,
    private servicioService: ServicioService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.paqueteId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.paqueteId;

    this.loadServicios();

    if (this.isEditMode && this.paqueteId) {
      this.loadPaquete(this.paqueteId);
    }
  }

  loadServicios(): void {
    this.servicioService.getAllServicios().subscribe({
      next: (data) => {
        this.serviciosDisponibles = data;
      },
      error: (err) => {
        this.error = 'Error al cargar servicios: ' + err.message;
      }
    });
  }

  loadPaquete(id: string): void {
    this.loading = true;
    this.paqueteTuristicoService.getPaqueteTuristicoById(id).subscribe({
      next: (data) => {
        // Asegurarse de que todas las propiedades tengan valores por defecto
        this.paquete = {
          id: data.id,
          nombrePaquete: data.nombrePaquete || '',
          descripcion: data.descripcion || '',
          destinoPrincipal: data.destinoPrincipal || '',
          duracionDias: data.duracionDias || undefined,
          precioTotalVenta: data.precioTotalVenta || undefined,
          servicios: data.servicios
        };
        // Obtener IDs de servicios ya asociados
        this.serviciosSeleccionados = data.servicios?.map(s => s.id!).filter(id => id) || [];
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar el paquete turístico: ' + err.message;
        this.loading = false;
      }
    });
  }

  toggleServicio(servicioId: string): void {
    const index = this.serviciosSeleccionados.indexOf(servicioId);
    if (index > -1) {
      this.serviciosSeleccionados.splice(index, 1);
    } else {
      this.serviciosSeleccionados.push(servicioId);
    }
  }

  isServicioSeleccionado(servicioId: string | undefined): boolean {
    return servicioId ? this.serviciosSeleccionados.includes(servicioId) : false;
  }

  getServiciosFiltrados(): Servicio[] {
    if (!this.searchServicio.trim()) {
      return this.serviciosDisponibles;
    }
    const term = this.searchServicio.toLowerCase();
    return this.serviciosDisponibles.filter(s =>
      s.nombreServicio.toLowerCase().includes(term) ||
      s.tipoServicio.toLowerCase().includes(term) ||
      (s.destinoCiudad && s.destinoCiudad.toLowerCase().includes(term))
    );
  }

  calcularPrecioTotal(): void {
    let total = 0;
    this.serviciosSeleccionados.forEach(id => {
      const servicio = this.serviciosDisponibles.find(s => s.id === id);
      if (servicio && servicio.precioVenta) {
        total += servicio.precioVenta;
      }
    });
    this.paquete.precioTotalVenta = total;
  }

  onSubmit(): void {
    if (!this.paquete.nombrePaquete || !this.paquete.destinoPrincipal) {
      this.error = 'Por favor, completa los campos requeridos';
      return;
    }

    this.loading = true;
    this.error = '';

    if (this.isEditMode && this.paqueteId) {
      this.paqueteTuristicoService.updatePaqueteTuristico(
        this.paqueteId,
        this.paquete,
        this.serviciosSeleccionados
      ).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/paquetes-turisticos']);
        },
        error: (err) => {
          this.error = 'Error al actualizar el paquete turístico: ' + err.message;
          this.loading = false;
        }
      });
    } else {
      this.paqueteTuristicoService.createPaqueteTuristico(
        this.paquete,
        this.serviciosSeleccionados
      ).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/paquetes-turisticos']);
        },
        error: (err) => {
          this.error = 'Error al crear el paquete turístico: ' + err.message;
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/dashboard/paquetes-turisticos']);
  }
}
