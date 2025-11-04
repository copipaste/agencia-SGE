import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ProveedorService } from '../../../services/proveedor.service';
import { CreateProveedorInput, UpdateProveedorInput } from '../../../models/proveedor.model';

@Component({
  selector: 'app-proveedor-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './proveedor-form.component.html',
  styleUrls: ['./proveedor-form.component.css']
})
export class ProveedorFormComponent implements OnInit {
  proveedorForm: FormGroup;
  isEditMode = false;
  proveedorId: string | null = null;
  loading = false;
  errorMessage = '';

  tiposServicio = ['Hotel', 'Vuelo', 'Tour', 'Transporte', 'Restaurante', 'Otro'];

  constructor(
    private fb: FormBuilder,
    private proveedorService: ProveedorService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.proveedorForm = this.fb.group({
      nombreEmpresa: ['', Validators.required],
      tipoServicio: ['', Validators.required],
      contactoNombre: [''],
      contactoEmail: ['', Validators.email],
      contactoTelefono: ['']
    });
  }

  ngOnInit(): void {
    this.proveedorId = this.route.snapshot.paramMap.get('id');
    
    if (this.proveedorId) {
      this.isEditMode = true;
      this.loadProveedor();
    }
  }

  loadProveedor(): void {
    if (!this.proveedorId) return;

    this.loading = true;
    this.proveedorService.getProveedorById(this.proveedorId).subscribe({
      next: (proveedor) => {
        this.proveedorForm.patchValue({
          nombreEmpresa: proveedor.nombreEmpresa,
          tipoServicio: proveedor.tipoServicio,
          contactoNombre: proveedor.contactoNombre || '',
          contactoEmail: proveedor.contactoEmail || '',
          contactoTelefono: proveedor.contactoTelefono || ''
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar proveedor:', error);
        this.errorMessage = 'Error al cargar los datos del proveedor';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.proveedorForm.invalid) {
      this.markFormGroupTouched(this.proveedorForm);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.proveedorForm.value;

    if (this.isEditMode && this.proveedorId) {
      const updateInput: UpdateProveedorInput = {
        nombreEmpresa: formValue.nombreEmpresa,
        tipoServicio: formValue.tipoServicio,
        contactoNombre: formValue.contactoNombre || undefined,
        contactoEmail: formValue.contactoEmail || undefined,
        contactoTelefono: formValue.contactoTelefono || undefined
      };

      this.proveedorService.updateProveedor(this.proveedorId, updateInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/proveedores']);
        },
        error: (error) => {
          console.error('Error al actualizar:', error);
          this.errorMessage = 'Error al actualizar el proveedor. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    } else {
      const createInput: CreateProveedorInput = {
        nombreEmpresa: formValue.nombreEmpresa,
        tipoServicio: formValue.tipoServicio,
        contactoNombre: formValue.contactoNombre || undefined,
        contactoEmail: formValue.contactoEmail || undefined,
        contactoTelefono: formValue.contactoTelefono || undefined
      };

      this.proveedorService.createProveedor(createInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/proveedores']);
        },
        error: (error) => {
          console.error('Error al crear:', error);
          this.errorMessage = 'Error al crear el proveedor. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/dashboard/proveedores']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.proveedorForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }
}
