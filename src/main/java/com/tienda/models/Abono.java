package com.tienda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "abono")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Abono {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_abono")
    private Long codAbono;

    // Relación ManyToOne con Fiado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_venta_fiado", nullable = false) // Columna FK
    private Fiado fiado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
}
