package com.tienda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "fiado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fiado {

    @Id
    @Column(name = "cod_venta_fiado") // La PK de Fiado es también la FK a Venta
    private Long codVentaFiado;

    // Relación OneToOne con Venta. @MapsId indica que el valor del ID de esta entidad
    // se tomará de la entidad "venta" a la que está mapeada.
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Esto significa que codVentaFiado es el mismo ID que el de la Venta
    @JoinColumn(name = "cod_venta_fiado", referencedColumnName = "cod_venta") // Explícitamente unimos con cod_venta de Venta
    private Venta venta; // La venta a la que este fiado está asociado

    // Relación ManyToOne con ClientePagoPendiente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_fiado", nullable = false) // Columna FK a ClientePagoPendiente
    private ClientePagoPendiente cliente;

    @Column(name = "estado", nullable = false, length = 20) // Ejemplo: "PENDIENTE", "PAGADO"
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion; // Fecha en que se generó el fiado

    // El monto total del fiado puede ser el mismo que el total de la venta,
    // o un valor inicial si se permite abonar parte. Lo mantendremos para flexibilidad.
    @Column(name = "monto_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoInicial;

    // Puedes añadir un campo para el monto pendiente si lo deseas, aunque se puede calcular
    // @Transient // No se persiste en la BD, es calculado
    // private BigDecimal montoPendiente;

    // Relación OneToMany con Abono
    @OneToMany(mappedBy = "fiado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Abono> abonos;
}