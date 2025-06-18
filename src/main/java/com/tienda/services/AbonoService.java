package com.tienda.services;

import com.tienda.models.Abono;
import com.tienda.models.Fiado;
import com.tienda.repositories.AbonoRepository;
import com.tienda.repositories.FiadoRepository; // Necesario para obtener el Fiado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AbonoService {

    @Autowired
    private AbonoRepository abonoRepository;

    @Autowired
    private FiadoRepository fiadoRepository;

    public List<Abono> getAllAbonos() {
        return abonoRepository.findAll();
    }

    public Optional<Abono> getAbonoById(Long id) {
        return abonoRepository.findById(id);
    }

    @Transactional
    public Abono createAbono(Long codVentaFiado, BigDecimal monto) {
        Fiado fiado = fiadoRepository.findById(codVentaFiado)
                .orElseThrow(() -> new RuntimeException("Fiado no encontrado con ID: " + codVentaFiado));

        Abono abono = new Abono();
        abono.setFiado(fiado);
        abono.setFecha(LocalDate.now());
        abono.setMonto(monto);

        // Lógica de negocio: Actualizar el estado del fiado si se ha pagado
        // completamente
        // Esto podría ser más complejo, calculando el total abonado vs. monto inicial
        BigDecimal totalAbonado = fiado.getAbonos().stream()
                .map(Abono::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal nuevoTotalAbonado = totalAbonado.add(monto);

        if (nuevoTotalAbonado.compareTo(fiado.getMontoInicial()) >= 0) {
            fiado.setEstado("PAGADO");
        } else {
            fiado.setEstado("PENDIENTE"); // Asegurar que el estado es pendiente si no se ha pagado todo
        }
        fiadoRepository.save(fiado); // Guardar el fiado actualizado

        return abonoRepository.save(abono);
    }

    @Transactional
    public Optional<Abono> updateAbono(Long id, Abono abonoDetails) {
        return abonoRepository.findById(id).map(abono -> {
            // Cuidado al modificar el monto de un abono, puede afectar el estado del fiado
            abono.setFecha(abonoDetails.getFecha());
            abono.setMonto(abonoDetails.getMonto());

            // Si el monto cambia, recalcula el estado del fiado
            Fiado fiado = abono.getFiado();
            BigDecimal totalAbonado = fiado.getAbonos().stream()
                    .map(Abono::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalAbonado.compareTo(fiado.getMontoInicial()) >= 0) {
                fiado.setEstado("PAGADO");
            } else {
                fiado.setEstado("PENDIENTE");
            }
            fiadoRepository.save(fiado);

            return abonoRepository.save(abono);
        });
    }

    public List<Abono> getAbonosByFiadoId(Long fiadoId) {
        return abonoRepository.findByFiadoCodVentaFiado(fiadoId);
    }

    public void deleteAbono(Long id) {
        // Considerar la lógica de negocio al eliminar un abono,
        // ya que podría cambiar el estado de un fiado de "PAGADO" a "PENDIENTE"
        Optional<Abono> abonoOptional = abonoRepository.findById(id);
        if (abonoOptional.isPresent()) {
            Abono abono = abonoOptional.get();
            Fiado fiado = abono.getFiado();
            abonoRepository.deleteById(id);

            // Recalcular el estado del fiado después de eliminar el abono
            BigDecimal totalAbonado = fiado.getAbonos().stream()
                    .filter(a -> !a.getCodAbono().equals(id)) // Excluir el abono que se elimina
                    .map(Abono::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalAbonado.compareTo(fiado.getMontoInicial()) >= 0) {
                fiado.setEstado("PAGADO");
            } else {
                fiado.setEstado("PENDIENTE");
            }
            fiadoRepository.save(fiado);
        }
    }
}
