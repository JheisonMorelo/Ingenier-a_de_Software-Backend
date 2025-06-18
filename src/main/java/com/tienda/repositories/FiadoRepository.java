package com.tienda.repositories;

import com.tienda.models.Fiado;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FiadoRepository extends JpaRepository<Fiado, Long> {
    List<Fiado> findByClienteId(Long clienteId);
}
