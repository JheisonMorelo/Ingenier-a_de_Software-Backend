// src/main/java/com/tienda/dto/VentaRequestDTO.java
package com.tienda.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid; // Para validar objetos anidados
import jakarta.validation.constraints.NotEmpty; // Para listas no vacías

import java.time.LocalDateTime;
import java.util.List;

// DTO para recibir los datos de una nueva venta o para actualizar una
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {
    private LocalDateTime fecha; // Opcional, si no se envía, el backend pondrá la fecha actual
    private Boolean pagada; // Si la venta se marca como pagada (true/false)

    @NotEmpty(message = "La venta debe contener al menos un detalle de producto")
    @Valid // Para que se validen los campos dentro de cada DetalleVentaRequestDTO
    private List<DetalleVentaRequestDTO> detallesVenta;
}
