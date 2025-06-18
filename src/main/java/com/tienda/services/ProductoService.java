package com.tienda.services;

import com.tienda.models.Producto;
import com.tienda.models.Proveedor;
import com.tienda.repositories.ProductoRepository;
import com.tienda.repositories.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProveedorRepository proveedorRepository; // Para asociar el producto a un proveedor

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto saveProducto(Producto producto) {

        // Validación: El precio del producto no puede ser negativo
        if (producto.getPrecio() != null && producto.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio del producto no puede ser negativo.");
        }

        // Asegurar que el proveedor exista
        if (producto.getProveedor() != null && producto.getProveedor().getId() != null) {
            Proveedor proveedor = proveedorRepository.findById(producto.getProveedor().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Proveedor no encontrado con ID: " + producto.getProveedor().getId()));
            producto.setProveedor(proveedor);
        } else {
            throw new IllegalArgumentException("El producto debe tener un proveedor asociado.");
        }
        return productoRepository.save(producto);
    }

    public void deleteProducto(Long id) {
        Optional<Producto> productoOptional = productoRepository.findById(id);
        if (productoOptional.isPresent()) {
            Producto producto = productoOptional.get();

            // Validación: No permitir eliminar si el producto está asociado a ventas
            if (producto.getDetallesVenta() != null && !producto.getDetallesVenta().isEmpty()) {
                throw new IllegalStateException("No se puede eliminar el producto porque está asociado a una o más ventas existentes.");
            }

            productoRepository.deleteById(id);
        } else {
            // Si el producto no se encuentra, podrías lanzar una excepción o simplemente no hacer nada
            // Depende de si el controlador ya maneja un notFound para el ID.
            throw new RuntimeException("Producto no encontrado con ID: " + id); // Lanzar excepción si no existe
        }
    }

    public Optional<Producto> updateProducto(Long id, Producto productoDetails) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoDetails.getNombre());

            // Validación al actualizar: El precio del producto no puede ser negativo
            if (productoDetails.getPrecio() != null && productoDetails.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El precio del producto no puede ser negativo.");
            }

            producto.setPrecio(productoDetails.getPrecio());
            producto.setStock(productoDetails.getStock());
            producto.setUnidad(productoDetails.getUnidad());

            // Actualizar el proveedor si se proporciona un nuevo ID de proveedor
            if (productoDetails.getProveedor() != null && productoDetails.getProveedor().getId() != null) {
                Proveedor proveedor = proveedorRepository.findById(productoDetails.getProveedor().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Proveedor no encontrado con ID: " + productoDetails.getProveedor().getId()));
                producto.setProveedor(proveedor);
            } else if (productoDetails.getProveedor() == null) {
                // Si se envía null para proveedor, puedes decidir si desasociarlo o lanzar un
                // error
                // En este caso, si no se envía, se mantiene el actual. Si se quiere desasociar,
                // se necesitaría una lógica explícita para eso (ej. enviar ID 0 o algo así).
            }

            return productoRepository.save(producto);
        });
    }
}