package com.tienda.repositories;

import com.tienda.models.ClientePagoPendiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientePagoPendienteRepository extends JpaRepository<ClientePagoPendiente, Long> {
    // Optional<ClientePagoPendiente> findByNombre(String nombre);
}
