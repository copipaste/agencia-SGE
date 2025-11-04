import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { VentaService } from '../../../services/venta.service';
import { ClienteService } from '../../../services/cliente.service';
import { AgenteService } from '../../../services/agente.service';
import { ServicioService } from '../../../services/servicio.service';
import { PaqueteTuristicoService } from '../../../services/paquete-turistico.service';
import { Venta } from '../../../models/venta.model';
import { Cliente } from '../../../models/cliente.model';
import { Agente } from '../../../models/agente.model';
import { Servicio } from '../../../models/servicio.model';
import { PaqueteTuristico } from '../../../models/paquete-turistico.model';
import { DetalleVentaInput } from '../../../models/detalle-venta.model';

interface DetalleFormItem {
  tipo: 'servicio' | 'paquete';
  servicioId: string;
  paqueteId: string;
  cantidad: number;
  precioUnitarioVenta: number;
  subtotal: number;
}

@Component({
  selector: 'app-venta-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './venta-form.component.html',
  styleUrl: './venta-form.component.css'
})
export class VentaFormComponent implements OnInit {
  venta: Venta = {
    id: '',
    clienteId: '',
    agenteId: '',
    fechaVenta: new Date().toISOString(),
    montoTotal: 0,
    estadoVenta: 'Pendiente',
    metodoPago: 'Efectivo'
  };

  detalles: DetalleFormItem[] = [];
  
  clientes: Cliente[] = [];
  agentes: Agente[] = [];
  servicios: Servicio[] = [];
  paquetes: PaqueteTuristico[] = [];
  
  isEditMode = false;
  loading = false;
  ventaId: string | null = null;

  constructor(
    private ventaService: VentaService,
    private clienteService: ClienteService,
    private agenteService: AgenteService,
    private servicioService: ServicioService,
    private paqueteService: PaqueteTuristicoService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.loadSelectors();
    this.ventaId = this.route.snapshot.paramMap.get('id');
    if (this.ventaId) {
      this.isEditMode = true;
      this.loadVenta(this.ventaId);
    } else {
      this.addDetalle();
    }
  }

  loadSelectors(): void {
    this.clienteService.getAllClientes().subscribe(data => this.clientes = data);
    this.agenteService.getAllAgentes().subscribe(data => this.agentes = data);
    this.servicioService.getAllServicios().subscribe(data => this.servicios = data);
    this.paqueteService.getAllPaquetesTuristicos().subscribe(data => this.paquetes = data);
  }

  loadVenta(id: string): void {
    this.loading = true;
    this.ventaService.getVentaById(id).subscribe({
      next: (data) => {
        this.venta = { ...data };
        this.detalles = data.detalles?.map(d => ({
          tipo: d.servicioId ? 'servicio' : 'paquete',
          servicioId: d.servicioId || '',
          paqueteId: d.paqueteId || '',
          cantidad: d.cantidad || 1,
          precioUnitarioVenta: d.precioUnitarioVenta || 0,
          subtotal: d.subtotal || 0
        })) || [];
        if (this.detalles.length === 0) this.addDetalle();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error:', error);
        this.loading = false;
      }
    });
  }

  addDetalle(): void {
    this.detalles.push({
      tipo: 'servicio',
      servicioId: '',
      paqueteId: '',
      cantidad: 1,
      precioUnitarioVenta: 0,
      subtotal: 0
    });
  }

  removeDetalle(index: number): void {
    this.detalles.splice(index, 1);
    this.calcularMontoTotal();
  }

  onTipoChange(detalle: DetalleFormItem): void {
    detalle.servicioId = '';
    detalle.paqueteId = '';
    detalle.precioUnitarioVenta = 0;
    detalle.subtotal = 0;
    this.calcularMontoTotal();
  }

  onItemChange(detalle: DetalleFormItem): void {
    if (detalle.tipo === 'servicio' && detalle.servicioId) {
      const servicio = this.servicios.find(s => s.id === detalle.servicioId);
      if (servicio && servicio.precioVenta !== undefined) {
        detalle.precioUnitarioVenta = servicio.precioVenta;
      }
    } else if (detalle.tipo === 'paquete' && detalle.paqueteId) {
      const paquete = this.paquetes.find(p => p.id === detalle.paqueteId);
      if (paquete && paquete.precioTotalVenta !== undefined) {
        detalle.precioUnitarioVenta = paquete.precioTotalVenta;
      }
    }
    this.calcularSubtotal(detalle);
  }

  onCantidadChange(detalle: DetalleFormItem): void {
    this.calcularSubtotal(detalle);
  }

  onPrecioChange(detalle: DetalleFormItem): void {
    this.calcularSubtotal(detalle);
  }

  calcularSubtotal(detalle: DetalleFormItem): void {
    detalle.subtotal = detalle.cantidad * detalle.precioUnitarioVenta;
    this.calcularMontoTotal();
  }

  calcularMontoTotal(): void {
    this.venta.montoTotal = this.detalles.reduce((sum, d) => sum + d.subtotal, 0);
  }

  onSubmit(): void {
    if (!this.venta.clienteId || !this.venta.agenteId) {
      alert('Debe seleccionar cliente y agente');
      return;
    }

    if (this.detalles.length === 0) {
      alert('Debe agregar al menos un detalle');
      return;
    }

    const detallesInput: DetalleVentaInput[] = this.detalles.map(d => ({
      servicioId: d.tipo === 'servicio' ? d.servicioId : null,
      paqueteId: d.tipo === 'paquete' ? d.paqueteId : null,
      cantidad: d.cantidad,
      precioUnitarioVenta: d.precioUnitarioVenta
    }));

    this.loading = true;

    if (this.isEditMode && this.ventaId) {
      this.ventaService.updateVenta(this.ventaId, {
        estadoVenta: this.venta.estadoVenta,
        metodoPago: this.venta.metodoPago
      }, detallesInput).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/dashboard/ventas']);
        },
        error: (error) => {
          console.error('Error:', error);
          alert('Error al actualizar la venta');
          this.loading = false;
        }
      });
    } else {
      this.ventaService.createVenta(this.venta, detallesInput).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/dashboard/ventas']);
        },
        error: (error) => {
          console.error('Error:', error);
          alert('Error al crear la venta');
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/dashboard/ventas']);
  }

  getClienteNombre(cliente: Cliente): string {
    return cliente.usuario ? 
      `${cliente.usuario.nombre} ${cliente.usuario.apellido}` : 
      'N/A';
  }

  getAgenteNombre(agente: Agente): string {
    return agente.usuario ? 
      `${agente.usuario.nombre} ${agente.usuario.apellido}` : 
      'N/A';
  }
}
