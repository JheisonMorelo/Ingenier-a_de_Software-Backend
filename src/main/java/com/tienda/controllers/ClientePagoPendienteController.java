package com.tienda.controllers;


import com.tienda.models.ClientePagoPendiente;
import com.tienda.services.ClientePagoPendienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes-pago-pendiente")
@CrossOrigin(origins = "http://localhost:4200")
public class ClientePagoPendienteController {

    @Autowired
    private ClientePagoPendienteService clientePagoPendienteService;

    @GetMapping
    public List<ClientePagoPendiente> getAllClientes() {
        return clientePagoPendienteService.getAllClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientePagoPendiente> getClienteById(@PathVariable Long id) {
        return clientePagoPendienteService.getClienteById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClientePagoPendiente> createCliente(@RequestBody ClientePagoPendiente cliente) {
        ClientePagoPendiente savedCliente = clientePagoPendienteService.saveCliente(cliente);
        return new ResponseEntity<>(savedCliente, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientePagoPendiente> updateCliente(@PathVariable Long id, @RequestBody ClientePagoPendiente clienteDetails) {
        return clientePagoPendienteService.updateCliente(id, clienteDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (clientePagoPendienteService.getClienteById(id).isPresent()) {
            clientePagoPendienteService.deleteCliente(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
