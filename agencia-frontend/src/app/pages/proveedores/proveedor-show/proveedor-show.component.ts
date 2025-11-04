import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProveedorService } from '../../../services/proveedor.service';
import { Proveedor } from '../../../models/proveedor.model';

@Component({
  selector: 'app-proveedor-show',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './proveedor-show.component.html',
  styleUrl: './proveedor-show.component.css'
})
export class ProveedorShowComponent implements OnInit {
  proveedor: Proveedor | null = null;
  loading = true;
  errorMessage = '';
  proveedorId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private proveedorService: ProveedorService
  ) {}

  ngOnInit(): void {
    this.proveedorId = this.route.snapshot.paramMap.get('id');
    
    if (this.proveedorId) {
      this.loadProveedor();
    } else {
      this.errorMessage = 'ID de proveedor no proporcionado';
      this.loading = false;
    }
  }

  loadProveedor(): void {
    if (!this.proveedorId) return;

    this.loading = true;
    this.proveedorService.getProveedorById(this.proveedorId).subscribe({
      next: (proveedor) => {
        this.proveedor = proveedor;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar proveedor:', error);
        this.errorMessage = 'Error al cargar los datos del proveedor';
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/proveedores']);
  }

  editProveedor(): void {
    if (this.proveedorId) {
      this.router.navigate(['/dashboard/proveedores/editar', this.proveedorId]);
    }
  }

  deleteProveedor(): void {
    if (!this.proveedorId) return;

    if (confirm('¿Estás seguro de que deseas eliminar este proveedor?')) {
      this.proveedorService.deleteProveedor(this.proveedorId).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/proveedores']);
        },
        error: (error) => {
          console.error('Error al eliminar proveedor:', error);
          this.errorMessage = 'Error al eliminar el proveedor';
        }
      });
    }
  }

  getInitials(): string {
    if (!this.proveedor) return '??';
    const words = this.proveedor.nombreEmpresa.split(' ');
    if (words.length >= 2) {
      return words[0].charAt(0) + words[1].charAt(0);
    }
    return this.proveedor.nombreEmpresa.substring(0, 2).toUpperCase();
  }
}
