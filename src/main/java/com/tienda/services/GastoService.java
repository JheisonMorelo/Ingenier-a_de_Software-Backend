package com.tienda.services;

import com.tienda.models.Gasto;
import com.tienda.repositories.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    public List<Gasto> getAllGastos() {
        return gastoRepository.findAll();
    }

    public Optional<Gasto> getGastoById(Long id) {
        return gastoRepository.findById(id);
    }

    public Gasto saveGasto(Gasto gasto) {
        if (gasto.getFecha() == null) {
            gasto.setFecha(LocalDate.now());
        }
        return gastoRepository.save(gasto);
    }

    public void deleteGasto(Long id) {
        gastoRepository.deleteById(id);
    }

    public Optional<Gasto> updateGasto(Long id, Gasto gastoDetails) {
        return gastoRepository.findById(id).map(gasto -> {
            gasto.setDescripcion(gastoDetails.getDescripcion());
            gasto.setCosto(gastoDetails.getCosto());
            gasto.setFecha(gastoDetails.getFecha());
            return gastoRepository.save(gasto);
        });
    }
}