package com.tienda.controllers;


import com.tienda.models.ContieneV;
import com.tienda.services.ContieneVService;
import com.tienda.models.ContieneVId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detaller-venta") // Puedes elegir un nombre más descriptivo como /api/detalles-venta
@CrossOrigin(origins = "http://localhost:4200")
public class ContieneVController {

    @Autowired
    private ContieneVService contieneVService;

    @GetMapping
    public List<ContieneV> getAllContieneV() {
        return contieneVService.getAllContieneV();
    }

    @GetMapping("/{codVenta}/{idProducto}")
    public ResponseEntity<ContieneV> getContieneVById(@PathVariable Long codVenta, @PathVariable Long idProducto) {
        ContieneVId id = new ContieneVId(codVenta, idProducto);
        return contieneVService.getContieneVById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createContieneV(@RequestParam Long codVenta,
                                                     @RequestParam Long idProducto,
                                                     @RequestParam Integer cantidad,
                                                     @RequestParam String unidad) {
        try {
            ContieneV savedContieneV = contieneVService.saveContieneV(codVenta, idProducto, cantidad, unidad);
            return new ResponseEntity<>(savedContieneV, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{codVenta}/{idProducto}")
    public ResponseEntity<ContieneV> updateContieneV(@PathVariable Long codVenta,
                                                     @PathVariable Long idProducto,
                                                     @RequestBody ContieneV contieneVDetails) {
        ContieneVId id = new ContieneVId(codVenta, idProducto);
        return contieneVService.updateContieneV(id, contieneVDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codVenta}/{idProducto}")
    public ResponseEntity<Void> deleteContieneV(@PathVariable Long codVenta, @PathVariable Long idProducto) {
        ContieneVId id = new ContieneVId(codVenta, idProducto);
        if (contieneVService.getContieneVById(id).isPresent()) {
            contieneVService.deleteContieneV(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
