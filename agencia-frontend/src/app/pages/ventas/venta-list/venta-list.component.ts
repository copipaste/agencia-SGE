import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { VentaService } from '../../../services/venta.service';
import { IAService } from '../../../services/ia.service';
import { Venta } from '../../../models/venta.model';
import { FilterPanelComponent, FilterOption, FilterValue } from '../../../components/filter-panel/filter-panel.component';
import { ExportButtonsComponent } from '../../../components/export-buttons/export-buttons.component';
import { ExportColumn } from '../../../services/export.service';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-venta-list',
  standalone: true,
  imports: [CommonModule, FormsModule, FilterPanelComponent, ExportButtonsComponent, ConfirmDialogComponent],
  templateUrl: './venta-list.component.html',
  styleUrl: './venta-list.component.css'
})
export class VentaListComponent implements OnInit {
  ventas: Venta[] = [];
  filteredVentas: Venta[] = [];
  paginatedVentas: Venta[] = [];
  loading = false;
  searchTerm = '';

  // Filtros
  filterOptions: FilterOption[] = [];
  activeFilters: FilterValue = {};

  // Exportación
  exportColumns: ExportColumn[] = [];

  // Modal de confirmación
  showConfirmDialog = false;
  confirmDialogData: ConfirmDialogData = {
    title: '',
    message: '',
    confirmButtonText: 'Eliminar',
    cancelButtonText: 'Cancelar'
  };
  ventaToDelete: string | null = null;

  // Paginación
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  Math = Math; // Para usar Math.min en el template
  Object = Object; // Para usar Object.keys en el template

  // Recordatorios IA
  enviandoRecordatorios = false;
  estadisticasIA: any = null;
  mostrarEstadisticasIA = false;

  constructor(
    private ventaService: VentaService,
    private iaService: IAService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.initializeFilters();
    this.initializeExportColumns();
    this.loadVentas();
    this.cargarEstadisticasIA();
  }

  initializeExportColumns(): void {
    this.exportColumns = [
      { 
        header: 'Fecha', 
        field: 'fechaVenta',
        format: (value) => value ? new Date(value).toLocaleDateString('es-ES') : 'N/A'
      },
      { header: 'Cliente', field: 'cliente.usuario.nombre' },
      { header: 'Agente', field: 'agente.usuario.nombre' },
      { header: 'Estado', field: 'estadoVenta' },
      { header: 'Método de Pago', field: 'metodoPago' },
      { 
        header: 'Monto Total', 
        field: 'montoTotal',
        format: (value) => value ? `$${value.toFixed(2)}` : '$0.00'
      },
      {
        header: 'Cantidad de Detalles',
        field: 'detalles',
        format: (value) => value ? value.length : 0
      }
    ];
  }

  initializeFilters(): void {
    this.filterOptions = [
      {
        key: 'estadoVenta',
        label: 'Estado de Venta',
        type: 'select',
        options: [
          { value: 'Pendiente', label: '⏳ Pendiente' },
          { value: 'Confirmada', label: '✅ Confirmada' },
          { value: 'Cancelada', label: '❌ Cancelada' },
          { value: 'Completada', label: '🎉 Completada' }
        ]
      },
      {
        key: 'metodoPago',
        label: 'Método de Pago',
        type: 'select',
        options: [
          { value: 'Efectivo', label: '💵 Efectivo' },
          { value: 'Tarjeta', label: '💳 Tarjeta' },
          { value: 'Transferencia', label: '🏦 Transferencia' },
          { value: 'QR', label: '📱 QR' }
        ]
      },
      {
        key: 'fechaInicio',
        label: 'Fecha Desde',
        type: 'date'
      },
      {
        key: 'fechaFin',
        label: 'Fecha Hasta',
        type: 'date'
      },
      {
        key: 'monto',
        label: 'Rango de Monto Total',
        type: 'range'
      }
    ];
  }

  loadVentas(): void {
    this.loading = true;
    this.ventaService.getAllVentas().subscribe({
      next: (data) => {
        this.ventas = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar ventas:', error);
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
    let filtered = [...this.ventas];

    // Aplicar búsqueda de texto
    if (this.searchTerm.trim()) {
      const searchLower = this.searchTerm.toLowerCase();
      filtered = filtered.filter(venta => {
        const clienteNombre = this.getClienteNombre(venta).toLowerCase();
        const agenteNombre = this.getAgenteNombre(venta).toLowerCase();
        const estado = (venta.estadoVenta || '').toLowerCase();
        const metodoPago = (venta.metodoPago || '').toLowerCase();
        
        return clienteNombre.includes(searchLower) ||
               agenteNombre.includes(searchLower) ||
               estado.includes(searchLower) ||
               metodoPago.includes(searchLower);
      });
    }

    // Filtros específicos
    if (this.activeFilters['estadoVenta']) {
      filtered = filtered.filter(v => v.estadoVenta === this.activeFilters['estadoVenta']);
    }

    if (this.activeFilters['metodoPago']) {
      filtered = filtered.filter(v => v.metodoPago === this.activeFilters['metodoPago']);
    }

    // Filtro de fecha desde
    if (this.activeFilters['fechaInicio']) {
      const fechaInicio = new Date(this.activeFilters['fechaInicio']);
      filtered = filtered.filter(v => {
        if (!v.fechaVenta) return false;
        const fechaVenta = new Date(v.fechaVenta);
        return fechaVenta >= fechaInicio;
      });
    }

    // Filtro de fecha hasta
    if (this.activeFilters['fechaFin']) {
      const fechaFin = new Date(this.activeFilters['fechaFin']);
      fechaFin.setHours(23, 59, 59); // Incluir todo el día
      filtered = filtered.filter(v => {
        if (!v.fechaVenta) return false;
        const fechaVenta = new Date(v.fechaVenta);
        return fechaVenta <= fechaFin;
      });
    }

    // Filtro de rango de monto
    if (this.activeFilters['montoMin'] !== undefined && this.activeFilters['montoMin'] !== null) {
      filtered = filtered.filter(v => 
        (v.montoTotal || 0) >= this.activeFilters['montoMin']
      );
    }

    if (this.activeFilters['montoMax'] !== undefined && this.activeFilters['montoMax'] !== null) {
      filtered = filtered.filter(v => 
        (v.montoTotal || 0) <= this.activeFilters['montoMax']
      );
    }

    this.filteredVentas = filtered;
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
    this.updatePagination();
  }

  // Métodos de paginación
  updatePagination(): void {
    this.totalPages = Math.ceil(this.filteredVentas.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.paginatedVentas = this.filteredVentas.slice(startIndex, endIndex);
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

  viewVenta(id?: string): void {
    if (id) {
      this.router.navigate(['/dashboard/ventas/ver', id]);
    }
  }

  editVenta(id?: string): void {
    if (id) {
      this.router.navigate(['/dashboard/ventas/editar', id]);
    }
  }

  deleteVenta(id?: string): void {
    if (!id) return;
    
    // Buscar la venta completa para mostrar información
    const venta = this.ventas.find(v => v.id === id);
    if (!venta) return;
    
    this.ventaToDelete = id;
    this.confirmDialogData = {
      title: '¿Eliminar Venta?',
      message: '¿Estás seguro de que deseas eliminar esta venta?',
      itemName: `Venta #${id.substring(0, 8)}`,
      warningMessage: 'Esta acción eliminará todos los detalles asociados a esta venta.',
      confirmButtonText: 'Sí, Eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonClass: 'btn-danger',
      additionalInfo: [
        { label: 'Cliente', value: this.getClienteNombre(venta) },
        { label: 'Agente', value: this.getAgenteNombre(venta) },
        { label: 'Fecha', value: venta.fechaVenta ? new Date(venta.fechaVenta).toLocaleDateString('es-ES') : 'N/A' },
        { label: 'Estado', value: venta.estadoVenta || 'N/A' },
        { label: 'Monto Total', value: venta.montoTotal ? `$${venta.montoTotal.toFixed(2)}` : '$0.00' },
        { label: 'Método de Pago', value: venta.metodoPago || 'N/A' }
      ]
    };
    
    this.showConfirmDialog = true;
  }

  confirmDeleteVenta(): void {
    if (!this.ventaToDelete) return;
    
    this.ventaService.deleteVenta(this.ventaToDelete).subscribe({
      next: () => {
        this.showConfirmDialog = false;
        this.ventaToDelete = null;
        this.loadVentas();
      },
      error: (error) => {
        console.error('Error al eliminar venta:', error);
        this.showConfirmDialog = false;
        alert('Error al eliminar la venta');
      }
    });
  }

  cancelDeleteVenta(): void {
    this.showConfirmDialog = false;
    this.ventaToDelete = null;
  }

  notificarVenta(ventaId?: string): void {
    if (!ventaId) return;

    // Buscar la venta para validación
    const venta = this.ventas.find(v => v.id === ventaId);
    if (!venta) return;

    // No permitir notificación de ventas canceladas
    if (venta.estadoVenta === 'Cancelada') {
      alert('No se pueden enviar notificaciones de ventas canceladas');
      return;
    }

    // Confirmación antes de enviar
    if (!confirm(`¿Desea enviar la notificación de esta venta al cliente ${this.getClienteNombre(venta)}?`)) {
      return;
    }

    this.loading = true;
    this.ventaService.notificarVenta(ventaId).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.success) {
          alert(`✅ ${response.message}`);
        } else {
          alert(`❌ ${response.message}`);
        }
      },
      error: (error) => {
        this.loading = false;
        console.error('Error al enviar notificación:', error);
        alert('❌ Error al enviar la notificación. Por favor, intente nuevamente.');
      }
    });
  }

  nuevaVenta(): void {
    this.router.navigate(['/dashboard/ventas/nuevo']);
  }

  getClienteNombre(venta: Venta): string {
    return venta.cliente?.usuario ? 
      `${venta.cliente.usuario.nombre} ${venta.cliente.usuario.apellido}` : 
      'N/A';
  }

  getAgenteNombre(venta: Venta): string {
    return venta.agente?.usuario ? 
      `${venta.agente.usuario.nombre} ${venta.agente.usuario.apellido}` : 
      'N/A';
  }

  getEstadoBadgeClass(estado?: string): string {
    switch(estado) {
      case 'Confirmada': return 'badge-success';
      case 'Pendiente': return 'badge-warning';
      case 'Cancelada': return 'badge-danger';
      default: return 'badge-secondary';
    }
  }

  /**
   * Carga las estadísticas de recordatorios del microservicio IA
   */
  cargarEstadisticasIA(): void {
    this.iaService.obtenerEstadisticasRecordatorios().subscribe({
      next: (data) => {
        this.estadisticasIA = data;
      },
      error: (error) => {
        console.warn('No se pudieron cargar estadísticas de IA:', error);
        this.estadisticasIA = null;
      }
    });
  }

  /**
   * Alterna la visibilidad del panel de estadísticas de IA
   */
  toggleEstadisticasIA(): void {
    this.mostrarEstadisticasIA = !this.mostrarEstadisticasIA;
    if (this.mostrarEstadisticasIA && !this.estadisticasIA) {
      this.cargarEstadisticasIA();
    }
  }

  /**
   * Fuerza el envío inmediato de recordatorios de cancelación
   * a través del microservicio de IA
   */
  enviarRecordatoriosIA(): void {
    if (this.enviandoRecordatorios) return;

    const confirmMessage = '🤖 ENVÍO DE RECORDATORIOS INTELIGENTES\n\n' +
      '¿Desea enviar recordatorios de cancelación ahora?\n\n' +
      'El sistema IA enviará correos a los clientes con alta probabilidad de cancelar.\n\n' +
      '⚠️ Solo los agentes pueden ejecutar esta acción.';

    if (!confirm(confirmMessage)) return;

    this.enviandoRecordatorios = true;

    this.iaService.forzarEnvioRecordatorios().subscribe({
      next: (resultado) => {
        this.enviandoRecordatorios = false;

        if (resultado.success) {
          const enviados = resultado.detalles?.recordatorios_enviados || 0;
          const detalles = resultado.detalles?.detalles || [];
          
          let mensaje = `✅ RECORDATORIOS ENVIADOS EXITOSAMENTE\n\n`;
          mensaje += `📧 Total enviados: ${enviados}\n\n`;
          
          if (detalles.length > 0) {
            mensaje += '📋 Detalles:\n';
            detalles.forEach((detalle: any, index: number) => {
              mensaje += `\n${index + 1}. ${detalle.nombre}`;
              mensaje += `\n   Email: ${detalle.email}`;
              mensaje += `\n   Paquete: ${detalle.paquete}`;
              mensaje += `\n   Probabilidad: ${(detalle.probabilidad * 100).toFixed(1)}%`;
            });
          }
          
          alert(mensaje);
          this.cargarEstadisticasIA(); // Actualizar estadísticas
        } else {
          alert(`❌ ERROR AL ENVIAR RECORDATORIOS\n\n${resultado.mensaje || 'Error desconocido'}`);
        }
      },
      error: (error) => {
        console.error('Error al enviar recordatorios:', error);
        this.enviandoRecordatorios = false;
        
        let errorMsg = '❌ ERROR DE CONEXIÓN\n\n';
        
        if (error.message?.includes('403') || error.message?.includes('Forbidden')) {
          errorMsg += '🔒 Acceso denegado\n\n';
          errorMsg += 'Solo los usuarios con rol AGENTE pueden enviar recordatorios.\n\n';
          errorMsg += 'Por favor, verifica que estés autenticado con una cuenta de agente.';
        } else if (error.message?.includes('Network') || error.message?.includes('connect')) {
          errorMsg += 'No se pudo conectar con el servidor.\n\n';
          errorMsg += 'Verifica que:\n';
          errorMsg += '- Spring Boot esté corriendo en localhost:8080\n';
          errorMsg += '- FastAPI esté corriendo en localhost:8001\n';
          errorMsg += '- Tengas una conexión estable';
        } else {
          errorMsg += `Detalle: ${error.message || 'Error desconocido'}`;
        }
        
        alert(errorMsg);
      }
    });
  }
}
