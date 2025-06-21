// src/main/java/com/tienda/controllers/VentaController.java
package com.tienda.controllers;

import com.tienda.dto.VentaRequestDTO;
import com.tienda.dto.VentaResponseDTO;
import com.tienda.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid; // Para la validación de DTOs

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:4200") // Asegúrate que tu frontend Angular pueda acceder
public class VentaController {

    @Autowired
    private VentaService ventaService;

    /**
     * Obtiene todas las ventas.
     * @return Una lista de VentaResponseDTO.
     */
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> getAllVentas() {
        List<VentaResponseDTO> ventas = ventaService.getAllVentas();
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene una venta por su ID.
     * @param id El ID de la venta.
     * @return Un ResponseEntity con el VentaResponseDTO o un 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> getVentaById(@PathVariable Long id) {
        return ventaService.getVentaById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva venta.
     * @param ventaDto El DTO con los datos de la venta.
     * @return Un ResponseEntity con el VentaResponseDTO de la venta creada o un error 400 Bad Request/404 Not Found.
     */
    @PostMapping
    public ResponseEntity<Object> createVenta(@Valid @RequestBody VentaRequestDTO ventaDto) {
        try {
            VentaResponseDTO savedVenta = ventaService.createVenta(ventaDto);
            return new ResponseEntity<>(savedVenta, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Errores de validación como ID de producto nulo o cantidad <= 0
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Errores como producto no encontrado o stock insuficiente
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // Podría ser 400 Bad Request también
        }
    }

    /**
     * Actualiza una venta existente.
     * Nota: Este método solo actualiza los campos de la cabecera de la venta.
     * La lógica de actualización de los detalles de productos en una venta es compleja y no se incluye aquí directamente.
     * @param id El ID de la venta a actualizar.
     * @param ventaDetails El DTO con los datos a actualizar.
     * @return Un ResponseEntity con el VentaResponseDTO actualizado o un 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> updateVenta(@PathVariable Long id, @RequestBody VentaRequestDTO ventaDetails) {
        return ventaService.updateVenta(id, ventaDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Elimina una venta y revierte el stock de los productos.
     * @param id El ID de la venta a eliminar.
     * @return Un ResponseEntity con 204 No Content si la eliminación es exitosa o un 404 Not Found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        try {
            ventaService.deleteVenta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Venta no encontrada
        }
    }

    /**
     * Obtiene una lista de ventas pagadas para el cálculo de ganancias.
     * @return Una lista de VentaResponseDTO de ventas pagadas.
     */
    @GetMapping("/ganancias")
    public ResponseEntity<List<VentaResponseDTO>> getVentasForProfit() {
        List<VentaResponseDTO> ventasGanancia = ventaService.getVentasForProfitCalculation();
        return ResponseEntity.ok(ventasGanancia);
    }

    /**
     * Calcula el total de ganancias de todas las ventas pagadas.
     * @return Un ResponseEntity con el BigDecimal del total de ganancias.
     */
    @GetMapping("/ganancias/total")
    public ResponseEntity<BigDecimal> getTotalProfit() {
        BigDecimal totalProfit = ventaService.calculateTotalProfit();
        return ResponseEntity.ok(totalProfit);
    }
}
