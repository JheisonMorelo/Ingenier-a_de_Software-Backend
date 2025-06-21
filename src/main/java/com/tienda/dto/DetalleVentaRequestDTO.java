// src/main/java/com/tienda/dto/DetalleVentaRequestDTO.java
package com.tienda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull; // Para validación
import jakarta.validation.constraints.Min;   // Para validación

// DTO para recibir los detalles de cada producto en una solicitud de venta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaRequestDTO {
    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long idProducto;

    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // La unidad del producto la obtendremos de la entidad Producto
    // y el precio unitario también.
}
