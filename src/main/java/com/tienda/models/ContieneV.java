package com.tienda.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "contiene_v")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContieneV {

    // Si tu PK es compuesta (Codigo_venta + Id_producto), necesitas una clase ID embebida
    @EmbeddedId
    private ContieneVId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("codigoVenta") // Mapea la parte 'codigoVenta' del EmbeddedId a esta FK
    @JoinColumn(name = "codigo_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto") // Mapea la parte 'idProducto' del EmbeddedId a esta FK
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "unidad", nullable = false, length = 50)
    private String unidad; // La unidad en la que se vendió el producto en esta venta (ej. "kg", "unidad")

    // Constructor para facilidad de uso
    public ContieneV(Venta venta, Producto producto, Integer cantidad, String unidad) {
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.id = new ContieneVId(venta.getCodVenta(), producto.getId());
    }
}