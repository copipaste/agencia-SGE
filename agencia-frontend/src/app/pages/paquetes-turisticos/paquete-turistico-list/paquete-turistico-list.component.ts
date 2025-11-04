import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PaqueteTuristicoService } from '../../../services/paquete-turistico.service';
import { PaqueteTuristico } from '../../../models/paquete-turistico.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-paquete-turistico-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ConfirmDialogComponent],
  templateUrl: './paquete-turistico-list.component.html',
  styleUrls: ['./paquete-turistico-list.component.css']
})
export class PaqueteTuristicoListComponent implements OnInit {
  paquetesTuristicos: PaqueteTuristico[] = [];
  filteredPaquetes: PaqueteTuristico[] = [];
  paginatedPaquetes: PaqueteTuristico[] = [];
  searchTerm: string = '';
  loading: boolean = false;
  error: string = '';

  // Modal de confirmación
  showConfirmDialog = false;
  confirmDialogData: ConfirmDialogData = {
    title: '',
    message: '',
    confirmButtonText: 'Eliminar',
    cancelButtonText: 'Cancelar'
  };
  paqueteToDelete: { id: string; nombre: string } | null = null;

  // Paginación
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  Math = Math; // Para usar Math.min en el template

  constructor(
    private paqueteTuristicoService: PaqueteTuristicoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPaquetesTuristicos();
  }

  loadPaquetesTuristicos(): void {
    this.loading = true;
    this.error = '';
    this.paqueteTuristicoService.getAllPaquetesTuristicos().subscribe({
      next: (data) => {
        this.paquetesTuristicos = data;
        this.filteredPaquetes = data;
        this.updatePagination();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar los paquetes turísticos: ' + err.message;
        this.loading = false;
      }
    });
  }

  searchPaquetes(): void {
    if (this.searchTerm.trim() === '') {
      this.filteredPaquetes = this.paquetesTuristicos;
      this.currentPage = 1;
      this.updatePagination();
      return;
    }

    this.loading = true;
    this.paqueteTuristicoService.searchPaquetesTuristicos(this.searchTerm).subscribe({
      next: (data) => {
        this.filteredPaquetes = data;
        this.currentPage = 1;
        this.updatePagination();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error en la búsqueda: ' + err.message;
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredPaquetes = this.paquetesTuristicos;
    this.currentPage = 1;
    this.updatePagination();
  }

  // Métodos de paginación
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredPaquetes.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedPaquetes = this.filteredPaquetes.slice(startIndex, endIndex);
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    
    if (this.totalPages <= maxPagesToShow) {
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      const startPage = Math.max(1, this.currentPage - 2);
      const endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1);
      
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i);
      }
    }
    
    return pages;
  }

  viewPaquete(id: string | undefined): void {
    if (id) {
      this.router.navigate(['/dashboard/paquetes-turisticos/ver', id]);
    }
  }

  editPaquete(id: string | undefined): void {
    if (id) {
      this.router.navigate(['/dashboard/paquetes-turisticos/editar', id]);
    }
  }

  deletePaquete(id: string | undefined, nombre: string): void {
    if (!id) return;

    this.paqueteToDelete = { id, nombre };
    this.confirmDialogData = {
      title: 'Eliminar Paquete Turístico',
      message: '¿Estás seguro de que deseas eliminar este paquete turístico?',
      itemName: nombre,
      warningMessage: 'Se eliminarán todos los datos asociados a este paquete.',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar'
    };
    this.showConfirmDialog = true;
  }

  onConfirmDelete(): void {
    if (this.paqueteToDelete) {
      this.paqueteTuristicoService.deletePaqueteTuristico(this.paqueteToDelete.id).subscribe({
        next: (success) => {
          if (success) {
            this.loadPaquetesTuristicos();
          } else {
            this.error = 'No se pudo eliminar el paquete turístico';
          }
          this.showConfirmDialog = false;
          this.paqueteToDelete = null;
        },
        error: (err) => {
          this.error = 'Error al eliminar el paquete turístico: ' + err.message;
          this.showConfirmDialog = false;
          this.paqueteToDelete = null;
        }
      });
    }
  }

  onCancelDelete(): void {
    this.showConfirmDialog = false;
    this.paqueteToDelete = null;
  }

  getPrecioPorDia(paquete: PaqueteTuristico): number {
    if (paquete.precioTotalVenta && paquete.duracionDias && paquete.duracionDias > 0) {
      return paquete.precioTotalVenta / paquete.duracionDias;
    }
    return 0;
  }

  getNumServicios(paquete: PaqueteTuristico): number {
    return paquete.servicios?.length || 0;
  }
}
