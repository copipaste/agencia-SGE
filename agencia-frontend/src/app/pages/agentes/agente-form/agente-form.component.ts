import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AgenteService } from '../../../services/agente.service';
import { CreateAgenteInput, UpdateAgenteInput } from '../../../models/agente.model';

@Component({
  selector: 'app-agente-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './agente-form.component.html',
  styleUrls: ['./agente-form.component.css']
})
export class AgenteFormComponent implements OnInit {
  agenteForm: FormGroup;
  isEditMode = false;
  agenteId: string | null = null;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private agenteService: AgenteService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.agenteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      nombre: ['', Validators.required],
      apellido: ['', Validators.required],
      telefono: [''],
      sexo: [''],
      puesto: ['', Validators.required],
      fechaContratacion: ['']
    });
  }

  ngOnInit(): void {
    this.agenteId = this.route.snapshot.paramMap.get('id');
    
    if (this.agenteId) {
      this.isEditMode = true;
      this.loadAgente();
      // En modo edición, deshabilitar campos de usuario
      this.agenteForm.get('email')?.disable();
      this.agenteForm.get('password')?.disable();
      this.agenteForm.get('nombre')?.disable();
      this.agenteForm.get('apellido')?.disable();
      this.agenteForm.get('telefono')?.disable();
      this.agenteForm.get('sexo')?.disable();
    }
  }

  loadAgente(): void {
    if (!this.agenteId) return;

    this.loading = true;
    this.agenteService.getAgenteById(this.agenteId).subscribe({
      next: (agente) => {
        this.agenteForm.patchValue({
          puesto: agente.puesto,
          fechaContratacion: agente.fechaContratacion || ''
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar agente:', error);
        this.errorMessage = 'Error al cargar los datos del agente';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.agenteForm.invalid) {
      this.markFormGroupTouched(this.agenteForm);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const formValue = this.agenteForm.value;

    if (this.isEditMode && this.agenteId) {
      const updateInput: UpdateAgenteInput = {
        puesto: formValue.puesto
      };

      if (formValue.fechaContratacion) {
        updateInput.fechaContratacion = formValue.fechaContratacion;
      }

      this.agenteService.updateAgente(this.agenteId, updateInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/agentes']);
        },
        error: (error) => {
          console.error('Error al actualizar:', error);
          this.errorMessage = 'Error al actualizar el agente. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    } else {
      const createInput: CreateAgenteInput = {
        email: formValue.email,
        password: formValue.password,
        nombre: formValue.nombre,
        apellido: formValue.apellido,
        puesto: formValue.puesto
      };

      if (formValue.telefono) createInput.telefono = formValue.telefono;
      if (formValue.sexo) createInput.sexo = formValue.sexo;
      if (formValue.fechaContratacion) createInput.fechaContratacion = formValue.fechaContratacion;

      this.agenteService.createAgente(createInput).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/agentes']);
        },
        error: (error) => {
          console.error('Error al crear:', error);
          this.errorMessage = 'Error al crear el agente. Por favor, intenta de nuevo.';
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/dashboard/agentes']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.agenteForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }
}
