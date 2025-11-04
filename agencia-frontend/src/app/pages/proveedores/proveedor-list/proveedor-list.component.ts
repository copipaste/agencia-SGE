import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProveedorService } from '../../../services/proveedor.service';
import { Proveedor } from '../../../models/proveedor.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../components/confirm-dialog/confirm-dialog.component';
import { ExportButtonsComponent } from '../../../components/export-buttons/export-buttons.component';
import { ExportColumn } from '../../../services/export.service';

@Component({
  selector: 'app-proveedor-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ConfirmDialogComponent, ExportButtonsComponent],
  templateUrl: './proveedor-list.component.html',
  styleUrls: ['./proveedor-list.component.css']
})
export class ProveedorListComponent implements OnInit {
  proveedores: Proveedor[] = [];
  filteredProveedores: Proveedor[] = [];
  paginatedProveedores: Proveedor[] = [];
  loading = false;
  errorMessage = '';
  searchTerm = '';

  // Modal de confirmación
  showConfirmDialog = false;
  confirmDialogData: ConfirmDialogData = {
    title: '',
    message: '',
    confirmButtonText: 'Eliminar',
    cancelButtonText: 'Cancelar'
  };
  proveedorToDelete: { id: string; nombre: string } | null = null;

  // Exportación
  exportColumns: ExportColumn[] = [
    { header: 'ID', field: 'id' },
    { header: 'Nombre de Empresa', field: 'nombreEmpresa' },
    { header: 'Tipo de Servicio', field: 'tipoServicio' },
    { header: 'Contacto', field: 'contactoNombre' },
    { header: 'Teléfono', field: 'contactoTelefono' },
    { header: 'Email', field: 'contactoEmail' }
  ];

  // Paginación
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  Math = Math; // Para usar Math.min en el template

  constructor(
    private proveedorService: ProveedorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProveedores();
  }

  loadProveedores(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.proveedorService.getAllProveedores().subscribe({
      next: (proveedores) => {
        this.proveedores = proveedores;
        this.filteredProveedores = proveedores;
        this.updatePagination();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar proveedores:', error);
        this.errorMessage = 'Error al cargar la lista de proveedores';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredProveedores = this.proveedores;
      this.currentPage = 1;
      this.updatePagination();
      return;
    }

    this.loading = true;
    this.proveedorService.searchProveedores(this.searchTerm).subscribe({
      next: (proveedores) => {
        this.filteredProveedores = proveedores;
        this.currentPage = 1;
        this.updatePagination();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error en búsqueda:', error);
        this.errorMessage = 'Error al buscar proveedores';
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredProveedores = this.proveedores;
    this.currentPage = 1;
    this.updatePagination();
  }

  // Métodos de paginación
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredProveedores.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedProveedores = this.filteredProveedores.slice(startIndex, endIndex);
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

  viewProveedor(id: string): void {
    this.router.navigate(['/dashboard/proveedores/ver', id]);
  }

  editProveedor(id: string): void {
    this.router.navigate(['/dashboard/proveedores/editar', id]);
  }

  deleteProveedor(id: string, nombre: string): void {
    this.proveedorToDelete = { id, nombre };
    this.confirmDialogData = {
      title: 'Eliminar Proveedor',
      message: '¿Estás seguro de que deseas eliminar este proveedor?',
      itemName: nombre,
      warningMessage: 'Se eliminarán todos los datos asociados a este proveedor.',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar'
    };
    this.showConfirmDialog = true;
  }

  onConfirmDelete(): void {
    if (this.proveedorToDelete) {
      this.proveedorService.deleteProveedor(this.proveedorToDelete.id).subscribe({
        next: () => {
          this.loadProveedores();
          this.showConfirmDialog = false;
          this.proveedorToDelete = null;
        },
        error: (error) => {
          console.error('Error al eliminar:', error);
          alert('Error al eliminar el proveedor');
          this.showConfirmDialog = false;
          this.proveedorToDelete = null;
        }
      });
    }
  }

  onCancelDelete(): void {
    this.showConfirmDialog = false;
    this.proveedorToDelete = null;
  }
}
