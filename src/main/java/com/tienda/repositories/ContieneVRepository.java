package com.tienda.repositories;

import com.tienda.models.ContieneV;
import com.tienda.models.ContieneVId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import java.util.List;

@Repository
public interface ContieneVRepository extends JpaRepository<ContieneV, ContieneVId> {
    // Puedes añadir métodos personalizados para buscar detalles de venta
    // List<ContieneV> findByVentaCodVenta(Long codVenta);
    // List<ContieneV> findByProductoId(Long productoId);
}