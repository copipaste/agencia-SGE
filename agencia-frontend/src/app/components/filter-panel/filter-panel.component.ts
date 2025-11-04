import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface FilterOption {
  key: string;
  label: string;
  type: 'text' | 'select' | 'date' | 'number' | 'boolean' | 'range';
  options?: { value: any; label: string }[];
  placeholder?: string;
  value?: any;
  min?: number;
  max?: number;
}

export interface FilterValue {
  [key: string]: any;
}

@Component({
  selector: 'app-filter-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './filter-panel.component.html',
  styleUrls: ['./filter-panel.component.css']
})
export class FilterPanelComponent {
  @Input() filters: FilterOption[] = [];
  @Input() showPanel: boolean = true;
  @Output() filterChange = new EventEmitter<FilterValue>();
  @Output() clearFilters = new EventEmitter<void>();

  filterValues: FilterValue = {};

  ngOnInit() {
    // Inicializar valores de filtros
    this.filters.forEach(filter => {
      if (filter.value !== undefined) {
        this.filterValues[filter.key] = filter.value;
      }
    });
  }

  onFilterChange() {
    this.filterChange.emit(this.filterValues);
  }

  onClearFilters() {
    this.filterValues = {};
    this.filters.forEach(filter => {
      if (filter.type === 'boolean') {
        this.filterValues[filter.key] = undefined;
      }
    });
    this.clearFilters.emit();
  }

  hasActiveFilters(): boolean {
    return Object.values(this.filterValues).some(value => 
      value !== undefined && value !== null && value !== ''
    );
  }

  getActiveFiltersCount(): number {
    return Object.keys(this.filterValues).filter(k => {
      const value = this.filterValues[k];
      return value !== undefined && value !== null && value !== '';
    }).length;
  }

  getRangeValue(key: string, type: 'Min' | 'Max'): any {
    return this.filterValues[key + type];
  }

  setRangeValue(key: string, type: 'Min' | 'Max', event: any): void {
    const value = event.target.value;
    this.filterValues[key + type] = value ? parseFloat(value) : undefined;
    this.onFilterChange();
  }

  togglePanel() {
    this.showPanel = !this.showPanel;
  }
}
