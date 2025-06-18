package com.tienda.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContieneVId implements Serializable {

    @Column(name = "codigo_venta")
    private Long codigoVenta;

    @Column(name = "id_producto")
    private Long idProducto;

    // Es crucial implementar equals y hashCode para IDs compuestos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContieneVId that = (ContieneVId) o;
        return Objects.equals(codigoVenta, that.codigoVenta) &&
               Objects.equals(idProducto, that.idProducto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoVenta, idProducto);
    }
}
