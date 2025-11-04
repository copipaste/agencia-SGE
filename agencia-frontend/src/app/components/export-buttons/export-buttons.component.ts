import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExportService, ExportColumn } from '../../services/export.service';

@Component({
  selector: 'app-export-buttons',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './export-buttons.component.html',
  styleUrls: ['./export-buttons.component.css']
})
export class ExportButtonsComponent {
  @Input() data: any[] = [];
  @Input() columns: ExportColumn[] = [];
  @Input() filename: string = 'export';
  @Input() printTitle: string = 'Reporte';
  @Input() showPrint: boolean = true;
  @Input() showExcel: boolean = true;
  @Input() showCSV: boolean = true;
  @Input() showJSON: boolean = false;

  showMenu: boolean = false;

  constructor(private exportService: ExportService) {}

  toggleMenu(): void {
    this.showMenu = !this.showMenu;
  }

  exportToExcel(): void {
    this.exportService.exportToExcel(this.data, this.columns, this.filename);
    this.showMenu = false;
  }

  exportToCSV(): void {
    this.exportService.exportToCSV(this.data, this.columns, this.filename);
    this.showMenu = false;
  }

  exportToJSON(): void {
    this.exportService.exportToJSON(this.data, this.filename);
    this.showMenu = false;
  }

  print(): void {
    this.exportService.print(this.data, this.columns, this.printTitle);
    this.showMenu = false;
  }

  get hasData(): boolean {
    return this.data && this.data.length > 0;
  }
}
