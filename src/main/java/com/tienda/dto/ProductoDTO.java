// src/main/java/com/tienda/dto/ProductoDTO.java
package com.tienda.dto;

import lombok.Data; // Para getters, setters, etc.
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

// DTO para representar la información de un producto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private String unidad;
    private ProveedorDTO proveedor; // Incluir el DTO del proveedor aquí
    // No incluir detallesVenta si no es necesario para este endpoint o si quieres controlarlo aparte
}
