package com.tienda.services;

import com.tienda.models.ClientePagoPendiente;
import com.tienda.models.Fiado;
import com.tienda.models.Venta;
import com.tienda.repositories.FiadoRepository;
import com.tienda.repositories.VentaRepository; // Necesario para obtener la Venta
import com.tienda.repositories.ClientePagoPendienteRepository; // Necesario para obtener el Cliente
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FiadoService {

    @Autowired
    private FiadoRepository fiadoRepository;

    @Autowired
    private VentaRepository ventaRepository; // Para asociar el fiado a una venta existente

    @Autowired
    private ClientePagoPendienteRepository clientePagoPendienteRepository; // Para asociar el cliente

    public List<Fiado> getAllFiados() {
        return fiadoRepository.findAll();
    }

    public Optional<Fiado> getFiadoById(Long id) {
        return fiadoRepository.findById(id);
    }

    public List<Fiado> getFiadosByClienteId(Long clienteId) {
        return fiadoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public Fiado createFiado(Long codVenta, Long idCliente, String estado, BigDecimal montoInicial) {
        Venta venta = ventaRepository.findById(codVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + codVenta));

        ClientePagoPendiente cliente = clientePagoPendienteRepository.findById(idCliente)
                .orElseThrow(
                        () -> new RuntimeException("Cliente de pago pendiente no encontrado con ID: " + idCliente));

        if (fiadoRepository.existsById(codVenta)) {
            throw new RuntimeException("Ya existe un fiado para esta venta (ID: " + codVenta + ")");
        }

        Fiado fiado = new Fiado();
        fiado.setCodVentaFiado(codVenta); // El ID del fiado es el mismo que el de la venta
        fiado.setVenta(venta);
        fiado.setCliente(cliente);
        fiado.setEstado(estado);
        fiado.setFechaCreacion(LocalDate.now());
        fiado.setMontoInicial(montoInicial);

        venta.setPagada(false);

        return fiadoRepository.save(fiado);
    }

    @Transactional
    public Optional<Fiado> updateFiado(Long id, Fiado fiadoDetails) {
        return fiadoRepository.findById(id).map(fiado -> {
            // No se debe cambiar el ID (codVentaFiado) ya que es la FK a Venta
            fiado.setEstado(fiadoDetails.getEstado());
            fiado.setFechaCreacion(fiadoDetails.getFechaCreacion());
            fiado.setMontoInicial(fiadoDetails.getMontoInicial());

            // Actualizar referencias si es necesario (ej. cambiar cliente)
            if (fiadoDetails.getCliente() != null && fiadoDetails.getCliente().getId() != null) {
                clientePagoPendienteRepository.findById(fiadoDetails.getCliente().getId())
                        .ifPresent(fiado::setCliente);
            }

            return fiadoRepository.save(fiado);
        });
    }

    public void deleteFiado(Long id) {
        fiadoRepository.deleteById(id);
    }
}
