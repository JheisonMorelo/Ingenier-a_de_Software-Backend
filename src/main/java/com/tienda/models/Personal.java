package com.tienda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "personal") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Genera constructor sin argumentos
@AllArgsConstructor // Genera constructor con todos los argumentos
public class Personal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incrementable
    private Long id;

    @Column(name = "usuario", unique = true, nullable = false, length = 50)
    private String usuario;

    @Column(name = "contrasena", nullable = false, length = 255) // Longitud mayor para contraseñas hasheadas
    private String contrasena;

    @Column(name = "rol", nullable = false, length = 20) // Ejemplo: "ADMIN", "VENDEDOR"
    private String rol;
}