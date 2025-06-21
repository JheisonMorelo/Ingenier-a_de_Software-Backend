// src/main/java/com/tienda/services/VentaService.java
package com.tienda.services;

import com.tienda.models.ContieneV;
import com.tienda.models.ContieneVId; // Importar la clave compuesta
import com.tienda.models.Producto;
import com.tienda.models.Venta;
import com.tienda.repositories.VentaRepository;
import com.tienda.repositories.ProductoRepository;
import com.tienda.repositories.ContieneVRepository;
import com.tienda.dto.VentaRequestDTO; // Importar DTOs de Request
import com.tienda.dto.DetalleVentaRequestDTO;
import com.tienda.dto.VentaResponseDTO; // Importar DTOs de Response
import com.tienda.dto.DetalleVentaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional; // Importar Transactional
import org.slf4j.Logger; // Para logging
import org.slf4j.LoggerFactory; // Para logging

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ContieneVRepository contieneVRepository;

    /**
     * Convierte una entidad Venta a un VentaResponseDTO.
     * @param venta La entidad Venta a convertir.
     * @return Un VentaResponseDTO.
     */
    private VentaResponseDTO convertToDto(Venta venta) {
        List<DetalleVentaResponseDTO> detallesDto = new ArrayList<>();
        if (venta.getDetallesVenta() != null) {
            detallesDto = venta.getDetallesVenta().stream().map(detalle -> {
                Producto producto = detalle.getProducto(); // Asumimos que el producto está cargado
                BigDecimal subtotal = BigDecimal.ZERO;
                if (producto != null && producto.getPrecio() != null && detalle.getCantidad() != null) {
                    subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                }
                return new DetalleVentaResponseDTO(
                    producto != null ? producto.getId() : null,
                    producto != null ? producto.getNombre() : "Producto Desconocido",
                    producto != null ? producto.getPrecio() : BigDecimal.ZERO,
                    detalle.getCantidad(),
                    detalle.getUnidad(),
                    subtotal
                );
            }).collect(Collectors.toList());
        }
        return new VentaResponseDTO(
            venta.getCodVenta(),
            venta.getFecha(),
            venta.getTotalPrecio(),
            venta.isPagada(),
            detallesDto
        );
    }

    /**
     * Obtiene todas las ventas.
     * @return Una lista de VentaResponseDTO.
     */
    @Transactional // Necesario para cargar Lazy relations (detallesVenta, producto dentro de detalle)
    public List<VentaResponseDTO> getAllVentas() {
        logger.info("Obteniendo todas las ventas...");
        return ventaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una venta por su ID.
     * @param id El ID de la venta.
     * @return Un Optional que contiene el VentaResponseDTO si se encuentra.
     */
    @Transactional // Necesario para cargar Lazy relations
    public Optional<VentaResponseDTO> getVentaById(Long id) {
        logger.info("Obteniendo venta con ID: {}", id);
        return ventaRepository.findById(id).map(this::convertToDto);
    }

    /**
     * Crea una nueva venta, gestionando el stock de productos.
     * @param ventaDto El DTO con los datos de la venta a crear.
     * @return El VentaResponseDTO de la venta creada.
     */
    @Transactional // ¡CRUCIAL! Asegura que todas las operaciones de DB sean atómicas
    public VentaResponseDTO createVenta(VentaRequestDTO ventaDto) {
        logger.info("Iniciando creación de nueva venta.");

        Venta venta = new Venta();
        venta.setFecha(ventaDto.getFecha() != null ? ventaDto.getFecha() : LocalDateTime.now());
        venta.setPagada(ventaDto.getPagada() != null ? ventaDto.getPagada() : false); // Por defecto no pagada
        venta.setTotalPrecio(BigDecimal.ZERO); // Se calculará sumando los subtotales de los productos

        // Guardar la venta inicialmente para obtener el codVenta
        Venta savedVenta = ventaRepository.save(venta);
        logger.debug("Venta inicial guardada con ID: {}", savedVenta.getCodVenta());

        BigDecimal totalCalculado = BigDecimal.ZERO;
        List<ContieneV> detallesParaGuardar = new ArrayList<>();

        if (ventaDto.getDetallesVenta() == null || ventaDto.getDetallesVenta().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un detalle de producto.");
        }

        for (DetalleVentaRequestDTO detalleDto : ventaDto.getDetallesVenta()) {
            if (detalleDto.getIdProducto() == null) {
                throw new IllegalArgumentException("El ID del producto en el detalle no puede ser nulo.");
            }
            if (detalleDto.getCantidad() == null || detalleDto.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad del producto debe ser positiva.");
            }

            Producto producto = productoRepository.findById(detalleDto.getIdProducto())
                    .orElseThrow(() -> {
                        logger.error("Producto no encontrado con ID: {}", detalleDto.getIdProducto());
                        return new RuntimeException("Producto no encontrado con ID: " + detalleDto.getIdProducto());
                    });

            if (producto.getStock() < detalleDto.getCantidad()) {
                logger.error("Stock insuficiente para el producto '{}'. Stock actual: {}, Cantidad solicitada: {}",
                             producto.getNombre(), producto.getStock(), detalleDto.getCantidad());
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            // Descontar stock
            producto.setStock(producto.getStock() - detalleDto.getCantidad());
            productoRepository.save(producto); // Guardar producto con stock actualizado
            logger.debug("Stock actualizado para producto '{}' (ID: {}). Nuevo stock: {}", producto.getNombre(), producto.getId(), producto.getStock());

            // Crear el detalle de venta
            ContieneV detalleVenta = new ContieneV();
            detalleVenta.setId(new ContieneVId(savedVenta.getCodVenta(), producto.getId()));
            detalleVenta.setVenta(savedVenta);
            detalleVenta.setProducto(producto);
            detalleVenta.setCantidad(detalleDto.getCantidad());
            detalleVenta.setUnidad(producto.getUnidad()); // Usar la unidad del producto

            detallesParaGuardar.add(detalleVenta);

            // Calcular y sumar al total de la venta
            BigDecimal subtotalDetalle = producto.getPrecio().multiply(BigDecimal.valueOf(detalleDto.getCantidad()));
            totalCalculado = totalCalculado.add(subtotalDetalle);
        }

        savedVenta.setDetallesVenta(detallesParaGuardar); // Asociar los detalles a la venta
        savedVenta.setTotalPrecio(totalCalculado); // Establecer el total calculado
        ventaRepository.save(savedVenta); // Guardar la venta final (con total y detalles asociados)
        logger.info("Venta creada exitosamente con ID: {} y Total: {}", savedVenta.getCodVenta(), savedVenta.getTotalPrecio());

        contieneVRepository.saveAll(detallesParaGuardar); // Guardar todos los detalles de la venta

        return convertToDto(savedVenta); // Convertir a DTO de respuesta
    }

    /**
     * Actualiza una venta existente.
     * Nota: La actualización de los detalles de venta (productos dentro de la venta) es compleja.
     * Este método se enfoca en actualizar los campos de la cabecera de la venta.
     * Para modificar los detalles, se podría considerar un endpoint específico o un manejo más granular.
     * @param id El ID de la venta a actualizar.
     * @param ventaDetails El DTO con los datos para actualizar.
     * @return Un Optional que contiene el VentaResponseDTO actualizado si se encuentra.
     */
    @Transactional
    public Optional<VentaResponseDTO> updateVenta(Long id, VentaRequestDTO ventaDetails) {
        logger.info("Actualizando venta con ID: {}", id);
        return ventaRepository.findById(id).map(venta -> {
            // Actualizar solo los campos de la cabecera de la venta
            if (ventaDetails.getFecha() != null) {
                venta.setFecha(ventaDetails.getFecha());
            }
            if (ventaDetails.getPagada() != null) {
                venta.setPagada(ventaDetails.getPagada());
            }
            // totalPrecio no se actualiza directamente aquí, debería recalcularse si los detalles cambian
            // o se asume que el frontend lo envía si la lógica de negocio lo permite.
            // Por simplicidad, no recalculamos totalPrecio aquí.
            
            // Si necesitas actualizar los detalles de venta aquí, la lógica es mucho más compleja:
            // 1. Revertir stock de los detalles viejos.
            // 2. Eliminar detalles viejos.
            // 3. Procesar nuevos detalles (como en createVenta: verificar stock, descontar, crear ContieneV).
            // Esto es propenso a errores y usualmente se maneja con devoluciones o anulaciones,
            // no con una "actualización" directa de los ítems de una venta ya realizada.
            // Para este caso, solo actualizamos los campos de la cabecera de la Venta.

            Venta updatedVenta = ventaRepository.save(venta);
            logger.info("Venta con ID {} actualizada.", id);
            return convertToDto(updatedVenta);
        });
    }

    /**
     * Elimina una venta y revierte el stock de los productos involucrados.
     * @param id El ID de la venta a eliminar.
     */
    @Transactional // ¡CRUCIAL! Asegura que la eliminación y la reversión de stock sean atómicas
    public void deleteVenta(Long id) {
        logger.info("Intentando eliminar venta con ID: {}", id);
        Optional<Venta> ventaOptional = ventaRepository.findById(id);
        if (ventaOptional.isPresent()) {
            Venta venta = ventaOptional.get();
            // Revertir stock de todos los productos en la venta
            for (ContieneV detalle : venta.getDetallesVenta()) { // Esto cargará los detalles (Lazy)
                Producto producto = detalle.getProducto(); // Esto cargará el producto (Lazy)
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto); // Guardar el producto con stock revertido
                logger.debug("Stock revertido para producto '{}' (ID: {}). Nuevo stock: {}", producto.getNombre(), producto.getId(), producto.getStock());
            }
            ventaRepository.deleteById(id);
            logger.info("Venta con ID {} eliminada exitosamente y stock revertido.", id);
        } else {
            logger.warn("Intento de eliminar venta con ID {} fallido: Venta no encontrada.", id);
            throw new RuntimeException("Venta no encontrada con ID: " + id); // Lanza excepción si no existe
        }
    }

    /**
     * Obtiene ventas que están marcadas como pagadas (para cálculo de ganancias).
     * @return Una lista de entidades Venta pagadas.
     */
    @Transactional // Necesario para cargar detalles si se usan en el cliente (aunque este es un método auxiliar para ganancias)
    public List<VentaResponseDTO> getVentasForProfitCalculation() {
        logger.info("Obteniendo ventas pagadas para cálculo de ganancias.");
        return ventaRepository.findByPagadaTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el total de ganancias de ventas pagadas.
     * @return El BigDecimal con el total de ganancias.
     */
    @Transactional // Se utiliza Transactional porque depende de findByPagadaTrue, que podría cargar detalles
    public BigDecimal calculateTotalProfit() {
        logger.info("Calculando ganancia total de ventas pagadas.");
        // Reutilizamos el método que obtiene las ventas pagadas, y luego sumamos sus totales.
        return ventaRepository.findByPagadaTrue().stream()
                .map(Venta::getTotalPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
