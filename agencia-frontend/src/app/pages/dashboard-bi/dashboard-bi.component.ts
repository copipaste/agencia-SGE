import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BiService, DashboardResumenResponse, SyncStatusResponse } from '../../services/bi.service';

@Component({
  selector: 'app-dashboard-bi',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard-bi.component.html',
  styleUrl: './dashboard-bi.component.css'
})
export class DashboardBiComponent implements OnInit {
  // Estado de carga
  loading = true;
  error: string | null = null;

  // Datos del dashboard
  dashboardData: DashboardResumenResponse | null = null;
  syncStatus: SyncStatusResponse | null = null;

  // Estado de salud
  healthStatus: 'checking' | 'healthy' | 'unhealthy' = 'checking';

  // Filtros de fecha
  fechaInicio: string = '';
  fechaFin: string = '';
  periodoActivo: 'todo' | 'hoy' | 'semana' | 'mes' | 'año' | 'personalizado' = 'todo';

  constructor(private biService: BiService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  /**
   * Carga todos los datos del dashboard
   */
  loadDashboardData(): void {
    this.loading = true;
    this.error = null;

    // Verificar que hay token
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No hay token JWT en localStorage');
      this.error = 'No estás autenticado. Por favor, vuelve a iniciar sesión.';
      this.loading = false;
      this.healthStatus = 'unhealthy';
      return;
    }

    console.log('🔑 Token JWT encontrado, cargando datos BI...');

    // 1. Verificar salud del servicio
    this.biService.checkHealth().subscribe({
      next: (health) => {
        console.log('✅ Health check exitoso:', health);
        this.healthStatus = health.status === 'ok' ? 'healthy' : 'unhealthy';
        
        // 2. Si está saludable, cargar el resto de datos
        if (this.healthStatus === 'healthy') {
          this.loadSyncStatus();
          this.loadDashboardResumen();
        } else {
          this.error = 'El servicio BI no está disponible';
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('❌ Error al verificar salud del servicio BI:', err);
        console.error('Detalles del error:', {
          status: err.status,
          statusText: err.statusText,
          message: err.message,
          url: err.url
        });
        this.healthStatus = 'unhealthy';
        
        // Mensajes de error más específicos
        if (err.status === 0) {
          this.error = 'No se puede conectar al backend. Verifica que esté corriendo en http://localhost:8080';
        } else if (err.status === 401 || err.status === 403) {
          this.error = 'No tienes permisos. Por favor, vuelve a iniciar sesión.';
        } else {
          this.error = `Error ${err.status}: ${err.statusText || 'Error al conectar con el servicio BI'}`;
        }
        
        this.loading = false;
      }
    });
  }

  /**
   * Carga el estado de sincronización
   */
  private loadSyncStatus(): void {
    this.biService.getSyncStatus().subscribe({
      next: (status) => {
        console.log('✅ Sync status obtenido:', status);
        this.syncStatus = status;
      },
      error: (err) => {
        console.error('⚠️ Error al obtener estado de sincronización:', err);
        // No es crítico, continuar
      }
    });
  }

  /**
   * Carga el resumen del dashboard con filtros de fecha opcionales
   */
  private loadDashboardResumen(): void {
    // Preparar parámetros de fecha según el período activo
    const fechaInicioParam = this.fechaInicio || undefined;
    const fechaFinParam = this.fechaFin || undefined;

    this.biService.getDashboardResumen(fechaInicioParam, fechaFinParam).subscribe({
      next: (data) => {
        console.log('✅ Dashboard resumen obtenido:', data);
        this.dashboardData = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Error al cargar dashboard:', err);
        console.error('Detalles:', {
          status: err.status,
          statusText: err.statusText,
          message: err.message
        });
        
        if (err.status === 401 || err.status === 403) {
          this.error = 'No tienes permisos para ver estos datos. Inicia sesión nuevamente.';
        } else if (err.status === 0) {
          this.error = 'No se puede conectar al backend. Verifica que esté corriendo.';
        } else {
          this.error = `Error al cargar los datos del dashboard (${err.status}). Por favor, intenta de nuevo.`;
        }
        
        this.loading = false;
      }
    });
  }

  /**
   * Reinicia la sincronización (solo admin)
   */
  restartSync(): void {
    if (confirm('¿Estás seguro de que deseas reiniciar la sincronización?')) {
      this.biService.restartSync().subscribe({
        next: (response) => {
          alert(response.message);
          this.loadSyncStatus(); // Recargar estado
        },
        error: (err) => {
          console.error('Error al reiniciar sincronización:', err);
          alert('Error al reiniciar la sincronización. Verifica tus permisos.');
        }
      });
    }
  }

  /**
   * Refresca todos los datos del dashboard
   */
  refresh(): void {
    this.loadDashboardData();
  }

  /**
   * Aplica filtro de período predefinido
   */
  setPeriodo(periodo: 'todo' | 'hoy' | 'semana' | 'mes' | 'año'): void {
    this.periodoActivo = periodo;
    const hoy = new Date();

    switch (periodo) {
      case 'todo':
        this.fechaInicio = '';
        this.fechaFin = '';
        break;

      case 'hoy':
        this.fechaInicio = this.formatDateToISO(hoy);
        this.fechaFin = this.formatDateToISO(hoy);
        break;

      case 'semana':
        const inicioSemana = new Date(hoy);
        inicioSemana.setDate(hoy.getDate() - hoy.getDay()); // Domingo
        this.fechaInicio = this.formatDateToISO(inicioSemana);
        this.fechaFin = this.formatDateToISO(hoy);
        break;

      case 'mes':
        const inicioMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        this.fechaInicio = this.formatDateToISO(inicioMes);
        this.fechaFin = this.formatDateToISO(hoy);
        break;

      case 'año':
        const inicioAño = new Date(hoy.getFullYear(), 0, 1);
        this.fechaInicio = this.formatDateToISO(inicioAño);
        this.fechaFin = this.formatDateToISO(hoy);
        break;
    }

    this.aplicarFiltros();
  }

  /**
   * Aplica filtros personalizados
   */
  aplicarFiltros(): void {
    this.loading = true;
    this.error = null;
    this.loadDashboardResumen();
  }

  /**
   * Limpia todos los filtros
   */
  limpiarFiltros(): void {
    this.fechaInicio = '';
    this.fechaFin = '';
    this.periodoActivo = 'todo';
    this.aplicarFiltros();
  }

  /**
   * Formatea una fecha a ISO (YYYY-MM-DD)
   */
  private formatDateToISO(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Maneja el cambio de fecha personalizada
   */
  onFechaChange(): void {
    if (this.fechaInicio || this.fechaFin) {
      this.periodoActivo = 'personalizado';
    }
  }

  /**
   * Formatea un número como moneda
   */
  formatCurrency(value: number): string {
    return new Intl.NumberFormat('es-BO', {
      style: 'currency',
      currency: 'BOB'
    }).format(value);
  }

  /**
   * Formatea un porcentaje
   */
  formatPercentage(value: number): string {
    return `${value.toFixed(2)}%`;
  }

  /**
   * Formatea una fecha
   */
  formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return new Intl.DateTimeFormat('es-BO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    }).format(date);
  }

  /**
   * Exporta las ventas a CSV
   */
  exportarCSV(): void {
    console.log('📥 Exportando ventas a CSV...');
    console.log('🔍 Período activo:', this.periodoActivo);
    console.log('📅 Fechas en filtro:', { 
      inicio: this.fechaInicio || 'Sin filtro', 
      fin: this.fechaFin || 'Sin filtro' 
    });
    
    // Usar las fechas de los filtros actuales si existen
    // Si están vacías, se exportará todo
    const fechaInicio = this.fechaInicio || undefined;
    const fechaFin = this.fechaFin || undefined;
    
    if (!fechaInicio && !fechaFin) {
      console.warn('⚠️ No hay filtros activos. Se exportarán TODAS las ventas.');
    } else {
      console.log('✅ Exportando con filtros:', { fechaInicio, fechaFin });
    }
    
    // Descargar CSV (abre en nueva pestaña)
    this.biService.descargarVentasCSV(fechaInicio, fechaFin);
  }
}
