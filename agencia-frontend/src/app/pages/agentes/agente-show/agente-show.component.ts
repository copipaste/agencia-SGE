import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AgenteService } from '../../../services/agente.service';
import { Agente } from '../../../models/agente.model';

@Component({
  selector: 'app-agente-show',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './agente-show.component.html',
  styleUrl: './agente-show.component.css'
})
export class AgenteShowComponent implements OnInit {
  agente: Agente | null = null;
  loading = true;
  errorMessage = '';
  agenteId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private agenteService: AgenteService
  ) {}

  ngOnInit(): void {
    this.agenteId = this.route.snapshot.paramMap.get('id');
    
    if (this.agenteId) {
      this.loadAgente();
    } else {
      this.errorMessage = 'ID de agente no proporcionado';
      this.loading = false;
    }
  }

  loadAgente(): void {
    if (!this.agenteId) return;

    this.loading = true;
    this.agenteService.getAgenteById(this.agenteId).subscribe({
      next: (agente) => {
        this.agente = agente;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar agente:', error);
        this.errorMessage = 'Error al cargar los datos del agente';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/agentes']);
  }

  editAgente(): void {
    if (this.agenteId) {
      this.router.navigate(['/dashboard/agentes/editar', this.agenteId]);
    }
  }

  deleteAgente(): void {
    if (!this.agenteId) return;

    if (confirm('¿Estás seguro de que deseas eliminar este agente?')) {
      this.agenteService.deleteAgente(this.agenteId).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/agentes']);
        },
        error: (error) => {
          console.error('Error al eliminar agente:', error);
          this.errorMessage = 'Error al eliminar el agente';
        }
      });
    }
  }

  formatDate(date: string | null | undefined): string {
    if (!date) return 'No especificada';
    
    const d = new Date(date);
    return d.toLocaleDateString('es-ES', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }

  calculateYearsWorked(fechaContratacion: string | null | undefined): number | null {
    if (!fechaContratacion) return null;
    
    const hireDate = new Date(fechaContratacion);
    const today = new Date();
    let years = today.getFullYear() - hireDate.getFullYear();
    const monthDiff = today.getMonth() - hireDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < hireDate.getDate())) {
      years--;
    }
    
    return years;
  }
}
