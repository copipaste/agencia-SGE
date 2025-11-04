import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteService } from '../../../services/cliente.service';
import { Cliente } from '../../../models/cliente.model';

@Component({
  selector: 'app-cliente-show',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cliente-show.component.html',
  styleUrl: './cliente-show.component.css'
})
export class ClienteShowComponent implements OnInit {
  cliente: Cliente | null = null;
  loading = true;
  errorMessage = '';
  clienteId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clienteService: ClienteService
  ) {}

  ngOnInit(): void {
    this.clienteId = this.route.snapshot.paramMap.get('id');
    
    if (this.clienteId) {
      this.loadCliente();
    } else {
      this.errorMessage = 'ID de cliente no proporcionado';
      this.loading = false;
    }
  }

  loadCliente(): void {
    if (!this.clienteId) return;

    this.loading = true;
    this.clienteService.getClienteById(this.clienteId).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar cliente:', error);
        this.errorMessage = 'Error al cargar los datos del cliente';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/clientes']);
  }

  editCliente(): void {
    if (this.clienteId) {
      this.router.navigate(['/dashboard/clientes/edit', this.clienteId]);
    }
  }

  deleteCliente(): void {
    if (!this.clienteId) return;

    if (confirm('¿Estás seguro de que deseas eliminar este cliente?')) {
      this.clienteService.deleteCliente(this.clienteId).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/clientes']);
        },
        error: (error) => {
          console.error('Error al eliminar cliente:', error);
          this.errorMessage = 'Error al eliminar el cliente';
        }
      });
    }
  }

  calculateAge(fechaNacimiento: string | null | undefined): number | null {
    if (!fechaNacimiento) return null;
    
    const birthDate = new Date(fechaNacimiento);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }
    
    return age;
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
}
