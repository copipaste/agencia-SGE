package com.agencia.agencia_backend.service;

import com.agencia.agencia_backend.dto.CreateProveedorInput;
import com.agencia.agencia_backend.dto.UpdateProveedorInput;
import com.agencia.agencia_backend.model.Proveedor;
import com.agencia.agencia_backend.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    /**
     * Obtener todos los proveedores
     */
    public List<Proveedor> getAllProveedores() {
        return proveedorRepository.findAll();
    }

    /**
     * Obtener proveedor por ID
     */
    public Optional<Proveedor> getProveedorById(String id) {
        return proveedorRepository.findById(id);
    }

    /**
     * Obtener proveedores por tipo de servicio
     */
    public List<Proveedor> getProveedoresByTipoServicio(String tipoServicio) {
        return proveedorRepository.findByTipoServicio(tipoServicio);
    }

    /**
     * Buscar proveedores por nombre de empresa
     */
    public List<Proveedor> searchProveedores(String searchTerm) {
        List<Proveedor> proveedores = proveedorRepository.findAll();
        
        return proveedores.stream()
            .filter(proveedor -> {
                String nombreEmpresa = proveedor.getNombreEmpresa().toLowerCase();
                String contactoNombre = proveedor.getContactoNombre() != null ? 
                    proveedor.getContactoNombre().toLowerCase() : "";
                return nombreEmpresa.contains(searchTerm.toLowerCase()) || 
                       contactoNombre.contains(searchTerm.toLowerCase());
            })
            .toList();
    }

    /**
     * Crear nuevo proveedor
     */
    public Proveedor createProveedor(CreateProveedorInput input) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreEmpresa(input.getNombreEmpresa());
        proveedor.setTipoServicio(input.getTipoServicio());
        proveedor.setContactoNombre(input.getContactoNombre());
        proveedor.setContactoEmail(input.getContactoEmail());
        proveedor.setContactoTelefono(input.getContactoTelefono());

        return proveedorRepository.save(proveedor);
    }

    /**
     * Actualizar proveedor existente
     */
    public Proveedor updateProveedor(String id, UpdateProveedorInput input) {
        Proveedor proveedor = proveedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        // Actualizar solo los campos que no son null
        if (input.getNombreEmpresa() != null) {
            proveedor.setNombreEmpresa(input.getNombreEmpresa());
        }
        if (input.getTipoServicio() != null) {
            proveedor.setTipoServicio(input.getTipoServicio());
        }
        if (input.getContactoNombre() != null) {
            proveedor.setContactoNombre(input.getContactoNombre());
        }
        if (input.getContactoEmail() != null) {
            proveedor.setContactoEmail(input.getContactoEmail());
        }
        if (input.getContactoTelefono() != null) {
            proveedor.setContactoTelefono(input.getContactoTelefono());
        }

        return proveedorRepository.save(proveedor);
    }

    /**
     * Eliminar proveedor (hard delete)
     */
    public boolean deleteProveedor(String id) {
        Proveedor proveedor = proveedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));

        proveedorRepository.delete(proveedor);
        return true;
    }
}
