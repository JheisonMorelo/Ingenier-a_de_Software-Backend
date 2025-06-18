package com.tienda.controllers;



import com.tienda.models.Fiado;
import com.tienda.services.FiadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fiados")
@CrossOrigin(origins = "http://localhost:4200")
public class FiadoController {

    @Autowired
    private FiadoService fiadoService;

    @GetMapping
    public List<Fiado> getAllFiados() {
        return fiadoService.getAllFiados();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fiado> getFiadoById(@PathVariable Long id) {
        return fiadoService.getFiadoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Fiado>> getFiadosByClienteId(@PathVariable Long clienteId) {
        List<Fiado> fiados = fiadoService.getFiadosByClienteId(clienteId);
        return ResponseEntity.ok(fiados);
    }

    // Para crear un fiado, necesitamos el ID de la Venta y el ID del Cliente
    @PostMapping
    public ResponseEntity<Object> createFiado(@RequestParam Long codVenta,
                                             @RequestParam Long idCliente,
                                             @RequestParam String estado,
                                             @RequestParam BigDecimal montoInicial) {
        try {
            Fiado savedFiado = fiadoService.createFiado(codVenta, idCliente, estado, montoInicial);
            return new ResponseEntity<>(savedFiado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fiado> updateFiado(@PathVariable Long id, @RequestBody Fiado fiadoDetails) {
        return fiadoService.updateFiado(id, fiadoDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiado(@PathVariable Long id) {
        if (fiadoService.getFiadoById(id).isPresent()) {
            fiadoService.deleteFiado(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
