package com.tienda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_venta")
    private Long codVenta;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "total_precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrecio;

    @Column(name = "pagada", nullable = false)
    private boolean pagada = true;

    // Relación OneToMany con ContieneV
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContieneV> detallesVenta;

    // La relación OneToOne con Fiado, indicando que Venta es el lado "propietario" de la relación
    // y Fiado tiene la clave foránea.
    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Fiado fiado; // Una venta puede tener un registro de fiado asociado
}
