import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

// ===================================
// INTERFACES (DTOs del Backend)
// ===================================

export interface HealthResponse {
  status: string;
}

export interface SyncStatusResponse {
  message: string;
  sync_enabled: boolean;
  sync_running: boolean;
}

export interface SyncRestartResponse {
  message: string;
  success: boolean;
}

export interface DashboardResumenResponse {
  periodo: {
    inicio: string | null;
    fin: string | null;
  };
  kpis: {
    total_clientes: number;
    total_ventas_confirmadas: number;
    total_ventas_pendientes: number;
    total_ventas_canceladas: number;
    total_ventas: number;
    total_monto_vendido: number;
    total_monto_pendiente: number;
    tasa_cancelacion: number;
  };
  top_destinos: Array<{
    destino: string;
    ingresos: number;
  }>;
  tendencia_reservas_por_dia: Array<{
    fecha: string;
    cantidad_reservas: number;
  }>;
}

export interface MargenBrutoResponse {
  margen_bruto_pct: number;
  ingresos: number;
  costo: number;
  periodo: {
    inicio: string | null;
    fin: string | null;
  };
}

export interface TasaConversionResponse {
  tasa_conversion_pct: number;
  periodo: {
    inicio: string | null;
    fin: string | null;
  };
}

export interface TasaCancelacionResponse {
  tasa_cancelacion_pct: number;
  periodo: {
    inicio: string | null;
    fin: string | null;
  };
}

export interface KpiResponse {
  nombre: string | null;
  valor: number | null;
  unidad: string | null;
  descripcion: string | null;
  periodo_inicio: string | null;
  periodo_fin: string | null;
}

/**
 * Servicio para interactuar con los endpoints de Business Intelligence
 * Conecta con el backend Spring Boot que a su vez se comunica con el servicio BI en Render
 */
@Injectable({
  providedIn: 'root'
})
export class BiService {
  private readonly API_URL = 'https://agencia-backend-app.azurewebsites.net/api/bi';

  constructor(private http: HttpClient) {}

  /**
   * Obtiene headers HTTP con el token JWT de autenticación
   */
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  // ===================================
  // ENDPOINTS PÚBLICOS
  // ===================================

  /**
   * Health check del servicio BI (endpoint público)
   * @returns Observable con el estado del servicio
   */
  checkHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(`${this.API_URL}/health`);
  }

  // ===================================
  // ENDPOINTS AUTENTICADOS
  // ===================================

  /**
   * Obtiene el estado de sincronización del servicio BI
   * @returns Observable con el estado de sincronización
   */
  getSyncStatus(): Observable<SyncStatusResponse> {
    return this.http.get<SyncStatusResponse>(
      `${this.API_URL}/sync/status`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Reinicia la sincronización del servicio BI (solo ADMIN)
   * @returns Observable con la respuesta del reinicio
   */
  restartSync(): Observable<SyncRestartResponse> {
    return this.http.post<SyncRestartResponse>(
      `${this.API_URL}/sync/restart`,
      {},
      { headers: this.getHeaders() }
    );
  }

  /**
   * Obtiene el resumen completo del dashboard con KPIs y tendencias
   * @param fechaInicio Fecha de inicio del período (formato YYYY-MM-DD) - Opcional
   * @param fechaFin Fecha de fin del período (formato YYYY-MM-DD) - Opcional
   * @returns Observable con el resumen del dashboard
   */
  getDashboardResumen(fechaInicio?: string, fechaFin?: string): Observable<DashboardResumenResponse> {
    let url = `${this.API_URL}/dashboard/resumen`;
    
    // Agregar parámetros de fecha si existen
    const params: string[] = [];
    if (fechaInicio) params.push(`fecha_inicio=${fechaInicio}`);
    if (fechaFin) params.push(`fecha_fin=${fechaFin}`);
    
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    
    return this.http.get<DashboardResumenResponse>(
      url,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Obtiene el KPI de margen bruto
   * @returns Observable con el margen bruto
   */
  getMargenBruto(): Observable<KpiResponse> {
    return this.http.get<KpiResponse>(
      `${this.API_URL}/kpi/margen-bruto`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Obtiene el KPI de tasa de conversión
   * @returns Observable con la tasa de conversión
   */
  getTasaConversion(): Observable<KpiResponse> {
    return this.http.get<KpiResponse>(
      `${this.API_URL}/kpi/tasa-conversion`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Obtiene el KPI de tasa de cancelación
   * @returns Observable con la tasa de cancelación
   */
  getTasaCancelacion(): Observable<KpiResponse> {
    return this.http.get<KpiResponse>(
      `${this.API_URL}/kpi/tasa-cancelacion`,
      { headers: this.getHeaders() }
    );
  }

  /**
   * Exporta las ventas a CSV
   * @param fechaInicio Fecha de inicio (opcional)
   * @param fechaFin Fecha de fin (opcional)
   * @returns URL para descargar el CSV
   */
  exportarVentasCSV(fechaInicio?: string, fechaFin?: string): string {
    let url = `${this.API_URL}/export/ventas.csv`;
    
    const params: string[] = [];
    if (fechaInicio) params.push(`fecha_inicio=${fechaInicio}`);
    if (fechaFin) params.push(`fecha_fin=${fechaFin}`);
    
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    
    return url;
  }

  /**
   * Descarga el archivo CSV de ventas
   * @param fechaInicio Fecha de inicio (opcional)
   * @param fechaFin Fecha de fin (opcional)
   */
  descargarVentasCSV(fechaInicio?: string, fechaFin?: string): void {
    const url = this.exportarVentasCSV(fechaInicio, fechaFin);
    console.log('📥 Abriendo URL de exportación:', url);
    window.open(url, '_blank');
  }
}

