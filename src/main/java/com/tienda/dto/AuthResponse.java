package com.tienda.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message; // Mensaje de éxito (ej: "Login exitoso")
    private String username;
    private List<String> roles;
}
