package com.tienda.services;

import com.tienda.dto.VentaReporteDTO;
import com.tienda.models.ContieneV;
import com.tienda.models.Producto;
import com.tienda.models.Venta;
import com.tienda.repositories.VentaRepository;
import com.tienda.repositories.ProductoRepository;
import com.tienda.repositories.ContieneVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ContieneVRepository contieneVRepository;

    public List<Venta> getAllVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> getVentaById(Long id) {
        return ventaRepository.findById(id);
    }

    @Transactional
    public Venta createVenta(Venta venta) {
        // Asegura que la fecha se establezca si no viene
        if (venta.getFecha() == null) {
            venta.setFecha(LocalDateTime.now());
        }
        venta.setTotalPrecio(BigDecimal.ZERO); // Inicializar total precio

        Venta savedVenta = ventaRepository.save(venta);

        // Procesar los detalles de venta (ContieneV) si vienen en la venta
        if (venta.getDetallesVenta() != null && !venta.getDetallesVenta().isEmpty()) {
            BigDecimal totalCalculado = BigDecimal.ZERO;
            List<ContieneV> detallesActualizados = new ArrayList<>();

            for (ContieneV detalle : venta.getDetallesVenta()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Producto no encontrado con ID: " + detalle.getProducto().getId()));

                if (producto.getStock() < detalle.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
                }
                
                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoRepository.save(producto);
                                
                // *** FIN LÓGICA DEL OBSERVADOR ***

                detalle.setVenta(savedVenta); // Asignar la venta recién creada
                detalle.setProducto(producto); // Asegurar que el objeto producto completo está en el detalle
                detalle.setId(new com.tienda.models.ContieneVId(savedVenta.getCodVenta(), producto.getId()));

                detallesActualizados.add(detalle);
                totalCalculado = totalCalculado
                        .add(producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad())));
            }
            savedVenta.setDetallesVenta(detallesActualizados); // Establecer la lista actualizada con objetos Producto
            savedVenta.setTotalPrecio(totalCalculado);
            contieneVRepository.saveAll(detallesActualizados); // Guardar los detalles después de guardar la venta
            ventaRepository.save(savedVenta); // Guardar la venta con el total precio actualizado
        }

        return savedVenta;
    }

    @Transactional
    public Optional<Venta> updateVenta(Long id, Venta ventaDetails) {
        return ventaRepository.findById(id).map(venta -> {
            venta.setFecha(ventaDetails.getFecha());
            venta.setTotalPrecio(ventaDetails.getTotalPrecio()); // Considera recalcular esto si se actualizan los
                                                                 // detalles

            // Lógica para actualizar los ContieneV (más compleja, implica borrar viejos y
            // añadir nuevos)
            // Por simplicidad en este ejemplo, no se implementa una actualización completa
            // de los detalles
            // si el frontend envía una lista de nuevos detalles para una venta existente.
            // Generalmente, las ventas no se "actualizan" en sus detalles, se cancelan y se
            // hacen nuevas,
            // o se manejan devoluciones. Si es necesario, esta lógica sería más compleja.

            return ventaRepository.save(venta);
        });
    }

    @Transactional
    public void deleteVenta(Long id) {
        // Lógica de negocio: Revertir stock al eliminar una venta
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isPresent()) {
            Venta venta = ventaOptional.get();
            for (ContieneV detalle : venta.getDetallesVenta()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
            ventaRepository.deleteById(id);
        }
    }

    public List<Venta> getVentasForProfitCalculation() {
        return ventaRepository.findByPagadaTrue();
    }

    public BigDecimal calculateTotalProfit() {
        return ventaRepository.findByPagadaTrue().stream()
                .map(Venta::getTotalPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

// MÉTODO ACTUALIZADO: Obtener datos detallados de ventas para el reporte general (por cada detalle de venta)
    @Transactional // Necesario para asegurar que se carguen los detalles de venta y los productos asociados
    public List<VentaReporteDTO> getGeneralSalesReportData() {
        List<Venta> allVentas = ventaRepository.findByPagadaTrue();
        List<VentaReporteDTO> reportData = new ArrayList<>();

        for (Venta venta : allVentas) {
            // Asegurarse de que los detalles de venta estén cargados
            // Si la relación OneToMany en Venta es LAZY, se cargarán aquí dentro de la transacción
            if (venta.getDetallesVenta() != null) {
                for (ContieneV detalle : venta.getDetallesVenta()) {
                    Producto producto = detalle.getProducto(); // El producto también debe estar cargado
                    
                    // Calcular subtotal de la línea
                    BigDecimal subtotalLinea = BigDecimal.ZERO;
                    if (producto != null && producto.getPrecio() != null && detalle.getCantidad() != null) {
                        subtotalLinea = producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    }

                    reportData.add(new VentaReporteDTO(
                            venta.getCodVenta(),
                            venta.getFecha(),
                            venta.getTotalPrecio(), // Total de la venta completa
                            venta.isPagada(),
                            producto != null ? producto.getId() : null,
                            producto != null ? producto.getNombre() : "Producto Desconocido",
                            producto != null ? producto.getPrecio() : BigDecimal.ZERO,
                            detalle.getCantidad(),
                            detalle.getUnidad(),
                            subtotalLinea // Subtotal de esta línea de producto
                    ));
                }
            } else {
                // Si una venta no tiene detalles de venta (poco probable en tu modelo, pero por si acaso)
                reportData.add(new VentaReporteDTO(
                        venta.getCodVenta(),
                        venta.getFecha(),
                        venta.getTotalPrecio(),
                        venta.isPagada(),
                        null, "Sin productos", BigDecimal.ZERO, 0, "", BigDecimal.ZERO
                ));
            }
        }
        return reportData;
    }
}
