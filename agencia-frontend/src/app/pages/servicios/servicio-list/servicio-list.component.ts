import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ServicioService } from '../../../services/servicio.service';
import { Servicio } from '../../../models/servicio.model';
import { FilterPanelComponent, FilterOption, FilterValue } from '../../../components/filter-panel/filter-panel.component';
import { ExportButtonsComponent } from '../../../components/export-buttons/export-buttons.component';
import { ExportColumn } from '../../../services/export.service';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-servicio-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, FilterPanelComponent, ExportButtonsComponent, ConfirmDialogComponent],
  templateUrl: './servicio-list.component.html',
  styleUrls: ['./servicio-list.component.css']
})
export class ServicioListComponent implements OnInit {
  servicios: Servicio[] = [];
  filteredServicios: Servicio[] = [];
  paginatedServicios: Servicio[] = [];
  loading = false;
  errorMessage = '';
  searchTerm = '';

  // Filtros
  filterOptions: FilterOption[] = [];
  activeFilters: FilterValue = {};

  // Configuración de exportación
  exportColumns: ExportColumn[] = [];

  // Modal de confirmación
  showConfirmDialog = false;
  confirmDialogData: ConfirmDialogData = {
    title: '',
    message: '',
    confirmButtonText: 'Eliminar',
    cancelButtonText: 'Cancelar'
  };
  servicioToDelete: { id: string; nombre: string } | null = null;

  // Paginación
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  Math = Math; // Para usar Math.min en el template
  Object = Object; // Para usar Object.keys en el template

  constructor(
    private servicioService: ServicioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeFilters();
    this.initializeExportColumns();
    this.loadServicios();
  }

  initializeExportColumns(): void {
    this.exportColumns = [
      { header: 'Tipo', field: 'tipoServicio' },
      { header: 'Nombre', field: 'nombreServicio' },
      { header: 'Descripción', field: 'descripcion' },
      { header: 'Proveedor', field: 'proveedor.nombreEmpresa' },
      { header: 'Destino Ciudad', field: 'destinoCiudad' },
      { header: 'Destino País', field: 'destinoPais' },
      { 
        header: 'Precio Costo', 
        field: 'precioCosto',
        format: (value) => value ? `$${value.toFixed(2)}` : 'N/A'
      },
      { 
        header: 'Precio Venta', 
        field: 'precioVenta',
        format: (value) => value ? `$${value.toFixed(2)}` : 'N/A'
      },
      { 
        header: 'Disponible', 
        field: 'isAvailable',
        format: (value) => value ? 'Sí' : 'No'
      }
    ];
  }

  initializeFilters(): void {
    this.filterOptions = [
      {
        key: 'tipoServicio',
        label: 'Tipo de Servicio',
        type: 'select',
        options: [
          { value: 'Vuelo', label: '✈️ Vuelo' },
          { value: 'Hotel', label: '🏨 Hotel' },
          { value: 'Tour', label: '🗺️ Tour' },
          { value: 'Transporte', label: '🚌 Transporte' },
          { value: 'Restaurante', label: '🍽️ Restaurante' }
        ]
      },
      {
        key: 'isAvailable',
        label: 'Disponibilidad',
        type: 'boolean'
      },
      {
        key: 'destinoCiudad',
        label: 'Ciudad Destino',
        type: 'text',
        placeholder: 'Ej: La Paz, Santa Cruz...'
      },
      {
        key: 'destinoPais',
        label: 'País',
        type: 'text',
        placeholder: 'Ej: Bolivia, Perú...'
      },
      {
        key: 'precio',
        label: 'Rango de Precio (Venta)',
        type: 'range'
      }
    ];
  }

  loadServicios(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.servicioService.getAllServicios().subscribe({
      next: (servicios) => {
        this.servicios = servicios;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar servicios:', error);
        this.errorMessage = 'Error al cargar la lista de servicios';
        this.loading = false;
      }
    });
  }

  onFilterChange(filters: FilterValue): void {
    this.activeFilters = filters;
    this.currentPage = 1;
    this.applyFilters();
  }

  onClearFilters(): void {
    this.activeFilters = {};
    this.searchTerm = '';
    this.currentPage = 1;
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = [...this.servicios];

    // Aplicar búsqueda de texto
    if (this.searchTerm.trim()) {
      const search = this.searchTerm.toLowerCase();
      filtered = filtered.filter(servicio =>
        servicio.nombreServicio?.toLowerCase().includes(search) ||
        servicio.descripcion?.toLowerCase().includes(search) ||
        servicio.destinoCiudad?.toLowerCase().includes(search)
      );
    }

    // Aplicar filtros
    if (this.activeFilters['tipoServicio']) {
      filtered = filtered.filter(s => s.tipoServicio === this.activeFilters['tipoServicio']);
    }

    if (this.activeFilters['isAvailable'] !== undefined && this.activeFilters['isAvailable'] !== null) {
      console.log('Filtro isAvailable:', this.activeFilters['isAvailable'], typeof this.activeFilters['isAvailable']);
      filtered = filtered.filter(s => s.isAvailable === this.activeFilters['isAvailable']);
    }

    if (this.activeFilters['destinoCiudad']) {
      const ciudad = this.activeFilters['destinoCiudad'].toLowerCase();
      filtered = filtered.filter(s => 
        s.destinoCiudad?.toLowerCase().includes(ciudad)
      );
    }

    if (this.activeFilters['destinoPais']) {
      const pais = this.activeFilters['destinoPais'].toLowerCase();
      filtered = filtered.filter(s => 
        s.destinoPais?.toLowerCase().includes(pais)
      );
    }

    // Filtro de rango de precio
    if (this.activeFilters['precioMin'] !== undefined && this.activeFilters['precioMin'] !== null) {
      filtered = filtered.filter(s => 
        (s.precioVenta || 0) >= this.activeFilters['precioMin']
      );
    }

    if (this.activeFilters['precioMax'] !== undefined && this.activeFilters['precioMax'] !== null) {
      filtered = filtered.filter(s => 
        (s.precioVenta || 0) <= this.activeFilters['precioMax']
      );
    }

    this.filteredServicios = filtered;
    this.updatePagination();
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applyFilters();
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.activeFilters = {};
    this.currentPage = 1;
    this.applyFilters();
  }

  // Métodos de paginación
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredServicios.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedServicios = this.filteredServicios.slice(startIndex, endIndex);
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

  viewServicio(id: string): void {
    this.router.navigate(['/dashboard/servicios/ver', id]);
  }

  editServicio(id: string): void {
    this.router.navigate(['/dashboard/servicios/editar', id]);
  }

  deleteServicio(id: string, nombre: string): void {
    // Buscar el servicio completo para mostrar más información
    const servicio = this.servicios.find(s => s.id === id);
    
    this.servicioToDelete = { id, nombre };
    this.confirmDialogData = {
      title: '¿Eliminar Servicio?',
      message: '¿Estás seguro de que deseas eliminar este servicio?',
      itemName: nombre,
      warningMessage: 'Este servicio podría estar asociado a paquetes turísticos.',
      confirmButtonText: 'Sí, Eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonClass: 'btn-danger',
      additionalInfo: servicio ? [
        { label: 'Tipo', value: servicio.tipoServicio || 'N/A' },
        { label: 'Proveedor', value: servicio.proveedor?.nombreEmpresa || 'N/A' },
        { label: 'Precio', value: servicio.precioVenta ? `$${servicio.precioVenta}` : 'N/A' },
        { label: 'Estado', value: servicio.isAvailable ? 'Disponible' : 'No Disponible' }
      ] : []
    };
    
    this.showConfirmDialog = true;
  }

  confirmDelete(): void {
    if (!this.servicioToDelete) return;
    
    this.servicioService.deleteServicio(this.servicioToDelete.id).subscribe({
      next: () => {
        this.showConfirmDialog = false;
        this.servicioToDelete = null;
        this.loadServicios();
      },
      error: (error) => {
        console.error('Error al eliminar:', error);
        this.errorMessage = 'Error al eliminar el servicio';
        this.showConfirmDialog = false;
      }
    });
  }

  cancelDelete(): void {
    this.showConfirmDialog = false;
    this.servicioToDelete = null;
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
}
