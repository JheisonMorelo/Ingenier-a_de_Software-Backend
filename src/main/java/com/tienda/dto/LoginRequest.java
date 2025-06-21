package com.tienda.dto;


// Importar Lombok si lo usas
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// DTO para recibir las credenciales de login del frontend
@Data // Anotación de Lombok para getters, setters, toString, equals, hashCode
@NoArgsConstructor // Anotación de Lombok para constructor sin argumentos
@AllArgsConstructor // Anotación de Lombok para constructor con todos los argumentos
public class LoginRequest {
    private String username;
    private String password;

}