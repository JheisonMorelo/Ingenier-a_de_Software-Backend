package com.tienda.services;

import com.tienda.dto.ProductoDTO;
import com.tienda.dto.ProveedorDTO;
import com.tienda.models.Producto;
import com.tienda.models.Proveedor;
import com.tienda.repositories.ProductoRepository;
import com.tienda.repositories.ProveedorRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProveedorRepository proveedorRepository; // Para asociar el producto a un proveedor

    
    /**
     * Convierte una entidad Producto a un ProductoDTO.
     * @param producto La entidad Producto.
     * @return El ProductoDTO correspondiente.
     */
    private ProductoDTO convertToDto(Producto producto) {
        ProveedorDTO proveedorDTO = null;
        if (producto.getProveedor() != null) {
            // Cargar el proveedor completo si es LAZY y necesitas sus detalles para el DTO
            // Asegúrate de que esta operación se haga dentro de una transacción activa
            // o que el fetch type sea EAGER para este caso si siempre lo necesitas.
            // Con LAZY, si no se carga aquí, el proxy aún podría causar problemas.
            // Una opción más robusta para LAZY es usar un DTO de creación/actualización con solo el ID del proveedor.
            // Para "getAllProductos", si quieres la info completa, podrías hacer un fetch join en el repo.
            
            // Para simplificar aquí, asumiremos que el proveedor ya está inicializado o que Spring/Hibernate lo maneja
            // al acceder a sus propiedades, o que el fetch type para esta relación en Producto.java es EAGER.
            // Si tu @ManyToOne es FetchType.LAZY y no hay una transacción abierta para inicializarlo,
            // esta línea podría lanzar LazyInitializationException.
            // Si la LazyInitializationException ocurre, considera:
            // 1. Cambiar `fetch = FetchType.EAGER` en la relación `@ManyToOne` en `Producto.java` para el `proveedor`.
            // 2. Usar un fetch join en el repositorio: `findBy...` con `@EntityGraph` o en una JPQL.
            // 3. Obtener el proveedor por ID aquí para asegurar que está cargado.
            // Por ahora, lo mantenemos simple asumiendo que se carga:
            proveedorDTO = new ProveedorDTO(
                producto.getProveedor().getId(),
                producto.getProveedor().getNombre(),
                producto.getProveedor().getTelefono()
            );
        }
        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getUnidad(),
            proveedorDTO
        );
    }
    
    @Transactional 
    public List<ProductoDTO> getAllProductosDTO() {
        return productoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

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