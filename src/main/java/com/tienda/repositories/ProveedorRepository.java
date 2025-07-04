package com.tienda.repositories;

import com.tienda.models.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    // Optional<Proveedor> findByNombre(String nombre);
}
