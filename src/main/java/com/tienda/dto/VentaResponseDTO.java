// src/main/java/com/tienda/dto/VentaResponseDTO.java
package com.tienda.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para enviar los datos completos de una venta como respuesta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    private Long codVenta;
    private LocalDateTime fecha;
    private BigDecimal totalPrecio;
    private Boolean pagada;
    private List<DetalleVentaResponseDTO> detallesVenta; // Lista de detalles de productos en la venta
}
