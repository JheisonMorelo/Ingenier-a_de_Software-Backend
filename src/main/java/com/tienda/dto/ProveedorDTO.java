// src/main/java/com/tienda/dto/ProveedorDTO.java
package com.tienda.dto;

import lombok.Data; // Para getters, setters, etc.
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// DTO para representar la información de un proveedor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTO {
    private Long id;
    private String nombre;
    private String telefono;
}