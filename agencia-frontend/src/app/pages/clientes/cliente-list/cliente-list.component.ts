import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../../services/cliente.service';
import { Cliente } from '../../../models/cliente.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../components/confirm-dialog/confirm-dialog.component';
import { ExportButtonsComponent } from '../../../components/export-buttons/export-buttons.component';
import { ExportColumn } from '../../../services/export.service';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ConfirmDialogComponent, ExportButtonsComponent],
  templateUrl: './cliente-list.component.html',
  styleUrls: ['./cliente-list.component.css']
})
export class ClienteListComponent implements OnInit {
  clientes: Cliente[] = [];
  filteredClientes: Cliente[] = [];
  paginatedClientes: Cliente[] = [];
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
  clienteToDelete: { id: string; nombre: string } | null = null;

  // Exportación
  exportColumns: ExportColumn[] = [
    { header: 'ID', field: 'id' },
    { header: 'Nombre', field: 'usuario.nombre' },
    { header: 'Apellido', field: 'usuario.apellido' },
    { header: 'Email', field: 'usuario.email' },
    { header: 'Teléfono', field: 'usuario.telefono' },
    { header: 'Dirección', field: 'direccion' },
    { header: 'Fecha de Nacimiento', field: 'fechaNacimiento', format: (value) => value ? new Date(value).toLocaleDateString('es-ES') : 'N/A' },
    { header: 'Número de Pasaporte', field: 'numeroPasaporte' }
  ];

  // Paginación
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  Math = Math; // Para usar Math.min en el template

  constructor(
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.clienteService.getAllClientes().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.filteredClientes = clientes;
        this.updatePagination();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar clientes:', error);
        this.errorMessage = 'Error al cargar la lista de clientes';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredClientes = this.clientes;
      this.currentPage = 1;
      this.updatePagination();
      return;
    }

    this.loading = true;
    this.clienteService.searchClientes(this.searchTerm).subscribe({
      next: (clientes) => {
        this.filteredClientes = clientes;
        this.currentPage = 1;
        this.updatePagination();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error en búsqueda:', error);
        this.errorMessage = 'Error al buscar clientes';
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredClientes = this.clientes;
    this.currentPage = 1;
    this.updatePagination();
  }

  // Métodos de paginación
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredClientes.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedClientes = this.filteredClientes.slice(startIndex, endIndex);
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

  viewCliente(id: string): void {
    this.router.navigate(['/dashboard/clientes/ver', id]);
  }

  editCliente(id: string): void {
    this.router.navigate(['/dashboard/clientes/editar', id]);
  }

  deleteCliente(id: string, nombre: string): void {
    this.clienteToDelete = { id, nombre };
    this.confirmDialogData = {
      title: 'Eliminar Cliente',
      message: '¿Estás seguro de que deseas eliminar este cliente?',
      itemName: nombre,
      warningMessage: 'Se eliminarán todos los datos asociados a este cliente.',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar'
    };
    this.showConfirmDialog = true;
  }

  onConfirmDelete(): void {
    if (this.clienteToDelete) {
      this.clienteService.deleteCliente(this.clienteToDelete.id).subscribe({
        next: () => {
          this.loadClientes();
          this.showConfirmDialog = false;
          this.clienteToDelete = null;
        },
        error: (error) => {
          console.error('Error al eliminar:', error);
          alert('Error al eliminar el cliente');
          this.showConfirmDialog = false;
          this.clienteToDelete = null;
        }
      });
    }
  }

  onCancelDelete(): void {
    this.showConfirmDialog = false;
    this.clienteToDelete = null;
  }

  getClienteNombreCompleto(cliente: Cliente): string {
    if (cliente.usuario) {
      return `${cliente.usuario.nombre} ${cliente.usuario.apellido}`;
    }
    return 'Cliente sin nombre';
  }
}
