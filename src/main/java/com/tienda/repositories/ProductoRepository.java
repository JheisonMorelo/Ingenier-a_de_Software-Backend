package com.tienda.repositories;

import com.tienda.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // List<Producto> findByNombreContainingIgnoreCase(String nombre);
    // List<Producto> findByStockLessThan(Integer stock); // Productos con bajo stock
}