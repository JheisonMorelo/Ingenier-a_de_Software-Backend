package com.tienda.services;

import com.tienda.models.Proveedor;
import com.tienda.repositories.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<Proveedor> getAllProveedores() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> getProveedorById(Long id) {
        return proveedorRepository.findById(id);
    }

    public Proveedor saveProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public void deleteProveedor(Long id) {
        proveedorRepository.deleteById(id);
    }

    public Optional<Proveedor> updateProveedor(Long id, Proveedor proveedorDetails) {
        return proveedorRepository.findById(id).map(proveedor -> {
            proveedor.setNombre(proveedorDetails.getNombre());
            proveedor.setTelefono(proveedorDetails.getTelefono());
            return proveedorRepository.save(proveedor);
        });
    }
}