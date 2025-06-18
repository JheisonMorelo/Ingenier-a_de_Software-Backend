package com.tienda.repositories;

import com.tienda.models.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.time.LocalDate;
//import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {
    // List<Gasto> findByFechaBetween(LocalDate startDate, LocalDate endDate);
}
