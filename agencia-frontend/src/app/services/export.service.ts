import { Injectable } from '@angular/core';

export interface ExportColumn {
  header: string;
  field: string;
  format?: (value: any) => string;
}

@Injectable({
  providedIn: 'root'
})
export class ExportService {

  /**
   * Exporta datos a formato CSV
   */
  exportToCSV(data: any[], columns: ExportColumn[], filename: string = 'export'): void {
    if (!data || data.length === 0) {
      alert('No hay datos para exportar');
      return;
    }

    // Crear encabezados
    const headers = columns.map(col => col.header).join(',');
    
    // Crear filas
    const rows = data.map(item => {
      return columns.map(col => {
        let value = this.getNestedValue(item, col.field);
        
        // Aplicar formato si existe
        if (col.format) {
          value = col.format(value);
        }
        
        // Escapar comas y comillas
        if (value === null || value === undefined) {
          return '';
        }
        
        const stringValue = String(value);
        if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n')) {
          return `"${stringValue.replace(/"/g, '""')}"`;
        }
        
        return stringValue;
      }).join(',');
    });

    // Combinar todo
    const csv = [headers, ...rows].join('\n');

    // Crear Blob y descargar
    const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
    this.downloadBlob(blob, `${filename}.csv`);
  }

  /**
   * Exporta datos a formato Excel (usando HTML table)
   */
  exportToExcel(data: any[], columns: ExportColumn[], filename: string = 'export'): void {
    if (!data || data.length === 0) {
      alert('No hay datos para exportar');
      return;
    }

    // Crear tabla HTML
    let html = '<html><head><meta charset="utf-8">';
    html += '<style>';
    html += 'table { border-collapse: collapse; width: 100%; }';
    html += 'th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }';
    html += 'th { background-color: #4CAF50; color: white; font-weight: bold; }';
    html += 'tr:nth-child(even) { background-color: #f2f2f2; }';
    html += '</style></head><body>';
    html += '<table>';

    // Encabezados
    html += '<thead><tr>';
    columns.forEach(col => {
      html += `<th>${col.header}</th>`;
    });
    html += '</tr></thead>';

    // Filas
    html += '<tbody>';
    data.forEach(item => {
      html += '<tr>';
      columns.forEach(col => {
        let value = this.getNestedValue(item, col.field);
        
        if (col.format) {
          value = col.format(value);
        }
        
        html += `<td>${value ?? ''}</td>`;
      });
      html += '</tr>';
    });
    html += '</tbody>';

    html += '</table></body></html>';

    // Crear Blob y descargar
    const blob = new Blob(['\ufeff' + html], { 
      type: 'application/vnd.ms-excel;charset=utf-8;' 
    });
    this.downloadBlob(blob, `${filename}.xls`);
  }

  /**
   * Exporta datos a formato JSON
   */
  exportToJSON(data: any[], filename: string = 'export'): void {
    if (!data || data.length === 0) {
      alert('No hay datos para exportar');
      return;
    }

    const json = JSON.stringify(data, null, 2);
    const blob = new Blob([json], { type: 'application/json;charset=utf-8;' });
    this.downloadBlob(blob, `${filename}.json`);
  }

  /**
   * Imprime el contenido actual (abre diálogo de impresión)
   */
  print(data: any[], columns: ExportColumn[], title: string = 'Reporte'): void {
    if (!data || data.length === 0) {
      alert('No hay datos para imprimir');
      return;
    }

    const printWindow = window.open('', '_blank');
    if (!printWindow) {
      alert('Por favor, permite las ventanas emergentes para imprimir');
      return;
    }

    let html = '<!DOCTYPE html><html><head>';
    html += '<meta charset="utf-8">';
    html += `<title>${title}</title>`;
    html += '<style>';
    html += 'body { font-family: Arial, sans-serif; margin: 20px; }';
    html += 'h1 { text-align: center; color: #333; }';
    html += 'table { border-collapse: collapse; width: 100%; margin-top: 20px; }';
    html += 'th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }';
    html += 'th { background-color: #4CAF50; color: white; font-weight: bold; }';
    html += 'tr:nth-child(even) { background-color: #f9f9f9; }';
    html += '@media print { body { margin: 0; } }';
    html += '</style>';
    html += '</head><body>';
    
    html += `<h1>${title}</h1>`;
    html += `<p>Fecha: ${new Date().toLocaleDateString('es-ES')}</p>`;
    html += `<p>Total de registros: ${data.length}</p>`;
    
    html += '<table>';
    
    // Encabezados
    html += '<thead><tr>';
    columns.forEach(col => {
      html += `<th>${col.header}</th>`;
    });
    html += '</tr></thead>';

    // Filas
    html += '<tbody>';
    data.forEach(item => {
      html += '<tr>';
      columns.forEach(col => {
        let value = this.getNestedValue(item, col.field);
        
        if (col.format) {
          value = col.format(value);
        }
        
        html += `<td>${value ?? ''}</td>`;
      });
      html += '</tr>';
    });
    html += '</tbody>';
    
    html += '</table>';
    html += '</body></html>';

    printWindow.document.write(html);
    printWindow.document.close();
    
    // Esperar a que cargue e imprimir
    setTimeout(() => {
      printWindow.focus();
      printWindow.print();
    }, 250);
  }

  /**
   * Obtiene un valor anidado de un objeto usando notación de punto
   * Ej: 'usuario.nombre' obtiene item.usuario.nombre
   */
  private getNestedValue(obj: any, path: string): any {
    return path.split('.').reduce((current, prop) => {
      return current?.[prop];
    }, obj);
  }

  /**
   * Descarga un Blob como archivo
   */
  private downloadBlob(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
