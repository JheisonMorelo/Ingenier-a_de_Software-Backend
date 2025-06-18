package com.tienda.services;

import com.tienda.models.ClientePagoPendiente;
import com.tienda.repositories.ClientePagoPendienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientePagoPendienteService {

    @Autowired
    private ClientePagoPendienteRepository clientePagoPendienteRepository;

    public List<ClientePagoPendiente> getAllClientes() {
        return clientePagoPendienteRepository.findAll();
    }

    public Optional<ClientePagoPendiente> getClienteById(Long id) {
        return clientePagoPendienteRepository.findById(id);
    }

    public ClientePagoPendiente saveCliente(ClientePagoPendiente cliente) {
        return clientePagoPendienteRepository.save(cliente);
    }

    public void deleteCliente(Long id) {
        clientePagoPendienteRepository.deleteById(id);
    }

    public Optional<ClientePagoPendiente> updateCliente(Long id, ClientePagoPendiente clienteDetails) {
        return clientePagoPendienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(clienteDetails.getNombre());
            return clientePagoPendienteRepository.save(cliente);
        });
    }
}