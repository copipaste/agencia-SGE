import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AgenteService } from '../../../services/agente.service';
import { Agente } from '../../../models/agente.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../components/confirm-dialog/confirm-dialog.component';
import { ExportButtonsComponent } from '../../../components/export-buttons/export-buttons.component';
import { ExportColumn } from '../../../services/export.service';

@Component({
  selector: 'app-agente-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ConfirmDialogComponent, ExportButtonsComponent],
  templateUrl: './agente-list.component.html',
  styleUrls: ['./agente-list.component.css']
})
export class AgenteListComponent implements OnInit {
  agentes: Agente[] = [];
  filteredAgentes: Agente[] = [];
  paginatedAgentes: Agente[] = [];
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
  agenteToDelete: { id: string; nombre: string } | null = null;

  // Exportación
  exportColumns: ExportColumn[] = [
    { header: 'ID', field: 'id' },
    { header: 'Nombre', field: 'usuario.nombre' },
    { header: 'Apellido', field: 'usuario.apellido' },
    { header: 'Email', field: 'usuario.email' },
    { header: 'Teléfono', field: 'usuario.telefono' },
    { header: 'Puesto', field: 'puesto' },
    { header: 'Fecha de Contratación', field: 'fechaContratacion', format: (value) => value ? new Date(value).toLocaleDateString('es-ES') : 'N/A' }
  ];

  // Paginación
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  Math = Math; // Para usar Math.min en el template

  constructor(
    private agenteService: AgenteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAgentes();
  }

  loadAgentes(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.agenteService.getAllAgentes().subscribe({
      next: (agentes) => {
        this.agentes = agentes;
        this.filteredAgentes = agentes;
        this.updatePagination();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar agentes:', error);
        this.errorMessage = 'Error al cargar la lista de agentes';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    if (!this.searchTerm.trim()) {
      this.filteredAgentes = this.agentes;
      this.currentPage = 1;
      this.updatePagination();
      return;
    }

    this.loading = true;
    this.agenteService.searchAgentes(this.searchTerm).subscribe({
      next: (agentes) => {
        this.filteredAgentes = agentes;
        this.currentPage = 1;
        this.updatePagination();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error en búsqueda:', error);
        this.errorMessage = 'Error al buscar agentes';
        this.loading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.filteredAgentes = this.agentes;
    this.currentPage = 1;
    this.updatePagination();
  }

  // Métodos de paginación
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredAgentes.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedAgentes = this.filteredAgentes.slice(startIndex, endIndex);
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

  viewAgente(id: string): void {
    this.router.navigate(['/dashboard/agentes/ver', id]);
  }

  editAgente(id: string): void {
    this.router.navigate(['/dashboard/agentes/editar', id]);
  }

  deleteAgente(id: string, nombre: string): void {
    this.agenteToDelete = { id, nombre };
    this.confirmDialogData = {
      title: 'Eliminar Agente',
      message: '¿Estás seguro de que deseas eliminar este agente?',
      itemName: nombre,
      warningMessage: 'Se eliminarán todos los datos asociados a este agente.',
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar'
    };
    this.showConfirmDialog = true;
  }

  onConfirmDelete(): void {
    if (this.agenteToDelete) {
      this.agenteService.deleteAgente(this.agenteToDelete.id).subscribe({
        next: () => {
          this.loadAgentes();
          this.showConfirmDialog = false;
          this.agenteToDelete = null;
        },
        error: (error) => {
          console.error('Error al eliminar:', error);
          alert('Error al eliminar el agente');
          this.showConfirmDialog = false;
          this.agenteToDelete = null;
        }
      });
    }
  }

  onCancelDelete(): void {
    this.showConfirmDialog = false;
    this.agenteToDelete = null;
  }

  getAgenteNombreCompleto(agente: Agente): string {
    if (agente.usuario) {
      return `${agente.usuario.nombre} ${agente.usuario.apellido}`;
    }
    return 'Agente sin nombre';
  }

  formatDate(date: string | null | undefined): string {
    if (!date) return 'No especificada';
    
    const d = new Date(date);
    return d.toLocaleDateString('es-ES', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }
}
