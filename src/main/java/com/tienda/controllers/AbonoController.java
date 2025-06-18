package com.tienda.controllers;

import com.tienda.models.Abono;
import com.tienda.services.AbonoService;
import com.tienda.services.FiadoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/abonos")
@CrossOrigin(origins = "http://localhost:4200")
public class AbonoController {

    @Autowired
    private AbonoService abonoService;

    @Autowired
    private FiadoService fiadoService;

    @GetMapping
    public List<Abono> getAllAbonos() {
        return abonoService.getAllAbonos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Abono> getAbonoById(@PathVariable Long id) {
        return abonoService.getAbonoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{fiadoId}/abonos")
    public ResponseEntity<List<Abono>> getAbonosByFiadoId(@PathVariable Long fiadoId) {
        // Primero, verifica si el fiado existe para retornar un 404 si no es así.
        // Si el fiado existe pero no tiene abonos, retorna una lista vacía (200 OK).
        if (!fiadoService.getFiadoById(fiadoId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<Abono> abonos = abonoService.getAbonosByFiadoId(fiadoId);
        return ResponseEntity.ok(abonos);
    }

    @PostMapping
    public ResponseEntity<Object> createAbono(@RequestParam Long codVentaFiado,
            @RequestParam BigDecimal monto) {
        try {
            Abono savedAbono = abonoService.createAbono(codVentaFiado, monto);
            return new ResponseEntity<>(savedAbono, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Abono> updateAbono(@PathVariable Long id, @RequestBody Abono abonoDetails) {
        return abonoService.updateAbono(id, abonoDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbono(@PathVariable Long id) {
        if (abonoService.getAbonoById(id).isPresent()) {
            abonoService.deleteAbono(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}