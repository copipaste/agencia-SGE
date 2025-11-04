import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ClienteService } from '../../../services/cliente.service';
import { AuthService } from '../../../services/auth.service';
import { CreateClienteInput, UpdateClienteInput } from '../../../models/cliente.model';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.css']
})
export class ClienteFormComponent implements OnInit {
  clienteForm: FormGroup;
  isEditMode = false;
  clienteId: string | null = null;
  loading = false;
  errorMessage = '';
  currentUserId = '';

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.clienteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      telefono: [''],
      sexo: [''],
      direccion: ['', Validators.required],
      fechaNacimiento: [''],
      numeroPasaporte: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.currentUserId = user.id;
      }
    });

    this.clienteId = this.route.snapshot.paramMap.get('id');
    
    if (this.clienteId) {
      this.isEditMode = true;
      this.loadCliente();
      // En modo edición, deshabilitar campos de usuario
      this.clienteForm.get('email')?.disable();
      this.clienteForm.get('password')?.disable();
      this.clienteForm.get('nombre')?.disable();
      this.clienteForm.get('apellido')?.disable();
      this.clienteForm.get('telefono')?.disable();
      this.clienteForm.get('sexo')?.disable();
    }
  }

  loadCliente(): void {
    if (!this.clienteId) return;

    this.loading = true;
    this.clienteService.getClienteById(this.clienteId).subscribe({
      next: (cliente) => {
        this.clienteForm.patchValue({
          direccion: cliente.direccion,
          fechaNacimiento: cliente.fechaNacimiento || '',
          numeroPasaporte: cliente.numeroPasaporte
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar cliente:', error);
        this.errorMessage = 'Error al cargar los datos del cliente';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.clienteForm.invalid) {
      this.markFormGroupTouched(this.clienteForm);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.clienteForm.value;

    if (this.isEditMode && this.clienteId) {
      const updateInput: UpdateClienteInput = {
        direccion: formValue.direccion,
        numeroPasaporte: formValue.numeroPasaporte
      };

      if (formValue.fechaNacimiento) updateInput.fechaNacimiento = formValue.fechaNacimiento;

      this.clienteService.updateCliente(this.clienteId, updateInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/clientes']);
        },
        error: (error) => {
          console.error('Error al actualizar:', error);
          this.errorMessage = 'Error al actualizar el cliente. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    } else {
      const createInput: CreateClienteInput = {
        email: formValue.email,
        password: formValue.password,
        nombre: formValue.nombre,
        apellido: formValue.apellido,
        direccion: formValue.direccion,
        numeroPasaporte: formValue.numeroPasaporte
      };

      if (formValue.telefono) createInput.telefono = formValue.telefono;
      if (formValue.sexo) createInput.sexo = formValue.sexo;
      if (formValue.fechaNacimiento) createInput.fechaNacimiento = formValue.fechaNacimiento;

      this.clienteService.createCliente(createInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/clientes']);
        },
        error: (error) => {
          console.error('Error al crear:', error);
          this.errorMessage = 'Error al crear el cliente. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/dashboard/clientes']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.clienteForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }
}
