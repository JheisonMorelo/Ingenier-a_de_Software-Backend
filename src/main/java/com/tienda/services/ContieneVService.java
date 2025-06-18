package com.tienda.services;


import com.tienda.models.ContieneV;
import com.tienda.models.ContieneVId;
import com.tienda.models.Venta;
import com.tienda.models.Producto;
import com.tienda.repositories.ContieneVRepository;
import com.tienda.repositories.VentaRepository;
import com.tienda.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContieneVService {

    @Autowired
    private ContieneVRepository contieneVRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<ContieneV> getAllContieneV() {
        return contieneVRepository.findAll();
    }

    public Optional<ContieneV> getContieneVById(ContieneVId id) {
        return contieneVRepository.findById(id);
    }

    @Transactional
    public ContieneV saveContieneV(Long codVenta, Long idProducto, Integer cantidad, String unidad) {
        Venta venta = ventaRepository.findById(codVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + codVenta));
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + idProducto));

        // Verificar stock antes de crear el detalle
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        // Descontar stock
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto); // Guardar el producto con stock actualizado

        ContieneVId id = new ContieneVId(codVenta, idProducto);
        ContieneV contieneV = new ContieneV(venta, producto, cantidad, unidad);
        contieneV.setId(id); // Set the embedded ID

        return contieneVRepository.save(contieneV);
    }

    @Transactional
    public Optional<ContieneV> updateContieneV(ContieneVId id, ContieneV contieneVDetails) {
        return contieneVRepository.findById(id).map(contieneV -> {
            // Lógica compleja para ajustar stock si la cantidad cambia.
            // Para simplificar, aquí solo actualizamos, pero en un sistema real,
            // si la cantidad cambia, deberías ajustar el stock anterior y el nuevo.
            contieneV.setCantidad(contieneVDetails.getCantidad());
            contieneV.setUnidad(contieneVDetails.getUnidad());
            return contieneVRepository.save(contieneV);
        });
    }

    @Transactional
    public void deleteContieneV(ContieneVId id) {
        // Lógica de negocio: Revertir stock al eliminar un detalle de venta
        Optional<ContieneV> contieneVOptional = contieneVRepository.findById(id);
        if (contieneVOptional.isPresent()) {
            ContieneV contieneV = contieneVOptional.get();
            Producto producto = contieneV.getProducto();
            producto.setStock(producto.getStock() + contieneV.getCantidad());
            productoRepository.save(producto);
            contieneVRepository.deleteById(id);
        }
    }
}