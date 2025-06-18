package com.tienda.repositories;


import com.tienda.models.Abono;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbonoRepository extends JpaRepository<Abono, Long> {
    List<Abono> findByFiadoCodVentaFiado(Long codVentaFiado);
}
