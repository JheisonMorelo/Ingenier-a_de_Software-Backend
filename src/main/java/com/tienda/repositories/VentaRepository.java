package com.tienda.repositories;

import com.tienda.models.Venta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByPagadaTrue();
}