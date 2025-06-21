// src/main/java/com/tienda/dto/DetalleVentaResponseDTO.java
package com.tienda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

// DTO para enviar los detalles de cada producto de una venta como respuesta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaResponseDTO {
    private Long idProducto;
    private String nombreProducto; // Nombre del producto para mayor claridad
    private BigDecimal precioUnitario; // Precio al momento de la venta
    private Integer cantidad;
    private String unidad; // Unidad del producto
    private BigDecimal subtotal; // Cantidad * Precio Unitario
}
