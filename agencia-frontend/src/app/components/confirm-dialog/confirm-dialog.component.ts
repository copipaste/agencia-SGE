import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface ConfirmDialogData {
  title: string;
  message: string;
  itemName?: string;
  warningMessage?: string;
  confirmButtonText?: string;
  cancelButtonText?: string;
  confirmButtonClass?: string;
  additionalInfo?: { label: string; value: string }[];
}

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent {
  @Input() show: boolean = false;
  @Input() data: ConfirmDialogData = {
    title: '¿Estás seguro?',
    message: 'Esta acción no se puede deshacer',
    confirmButtonText: 'Confirmar',
    cancelButtonText: 'Cancelar',
    confirmButtonClass: 'btn-danger'
  };
  
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

  onConfirm(): void {
    this.confirm.emit();
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.onCancel();
    }
  }
}
