import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ServicioService } from '../../../services/servicio.service';
import { ProveedorService } from '../../../services/proveedor.service';
import { CreateServicioInput, UpdateServicioInput } from '../../../models/servicio.model';
import { Proveedor } from '../../../models/proveedor.model';

@Component({
  selector: 'app-servicio-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './servicio-form.component.html',
  styleUrls: ['./servicio-form.component.css']
})
export class ServicioFormComponent implements OnInit {
  servicioForm: FormGroup;
  isEditMode = false;
  servicioId: string | null = null;
  loading = false;
  errorMessage = '';
  proveedores: Proveedor[] = [];

  tiposServicio = ['Vuelo', 'Hotel', 'Tour', 'Transporte', 'Restaurante', 'Otro'];

  constructor(
    private fb: FormBuilder,
    private servicioService: ServicioService,
    private proveedorService: ProveedorService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.servicioForm = this.fb.group({
      proveedorId: [''],
      tipoServicio: ['', Validators.required],
      nombreServicio: ['', Validators.required],
      descripcion: [''],
      destinoCiudad: [''],
      destinoPais: [''],
      precioCosto: [null, [Validators.min(0)]],
      precioVenta: [null, [Validators.min(0)]],
      isAvailable: [true]
    });
  }

  ngOnInit(): void {
    this.servicioId = this.route.snapshot.paramMap.get('id');
    this.loadProveedores();
    
    if (this.servicioId) {
      this.isEditMode = true;
      this.loadServicio();
    }
  }

  loadProveedores(): void {
    this.proveedorService.getAllProveedores().subscribe({
      next: (proveedores) => {
        this.proveedores = proveedores;
      },
      error: (error) => {
        console.error('Error al cargar proveedores:', error);
      }
    });
  }

  loadServicio(): void {
    if (!this.servicioId) return;

    this.loading = true;
    this.servicioService.getServicioById(this.servicioId).subscribe({
      next: (servicio) => {
        this.servicioForm.patchValue({
          proveedorId: servicio.proveedorId || '',
          tipoServicio: servicio.tipoServicio,
          nombreServicio: servicio.nombreServicio,
          descripcion: servicio.descripcion || '',
          destinoCiudad: servicio.destinoCiudad || '',
          destinoPais: servicio.destinoPais || '',
          precioCosto: servicio.precioCosto,
          precioVenta: servicio.precioVenta,
          isAvailable: servicio.isAvailable !== undefined ? servicio.isAvailable : true
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar servicio:', error);
        this.errorMessage = 'Error al cargar los datos del servicio';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.servicioForm.invalid) {
      this.markFormGroupTouched(this.servicioForm);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.servicioForm.value;

    if (this.isEditMode && this.servicioId) {
      const updateInput: UpdateServicioInput = {
        proveedorId: formValue.proveedorId || undefined,
        tipoServicio: formValue.tipoServicio,
        nombreServicio: formValue.nombreServicio,
        descripcion: formValue.descripcion || undefined,
        destinoCiudad: formValue.destinoCiudad || undefined,
        destinoPais: formValue.destinoPais || undefined,
        precioCosto: formValue.precioCosto || undefined,
        precioVenta: formValue.precioVenta || undefined,
        isAvailable: formValue.isAvailable
      };

      this.servicioService.updateServicio(this.servicioId, updateInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/servicios']);
        },
        error: (error) => {
          console.error('Error al actualizar:', error);
          this.errorMessage = 'Error al actualizar el servicio. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    } else {
      const createInput: CreateServicioInput = {
        proveedorId: formValue.proveedorId || undefined,
        tipoServicio: formValue.tipoServicio,
        nombreServicio: formValue.nombreServicio,
        descripcion: formValue.descripcion || undefined,
        destinoCiudad: formValue.destinoCiudad || undefined,
        destinoPais: formValue.destinoPais || undefined,
        precioCosto: formValue.precioCosto || undefined,
        precioVenta: formValue.precioVenta || undefined,
        isAvailable: formValue.isAvailable
      };

      this.servicioService.createServicio(createInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/servicios']);
        },
        error: (error) => {
          console.error('Error al crear:', error);
          this.errorMessage = 'Error al crear el servicio. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/dashboard/servicios']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.servicioForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }
}
