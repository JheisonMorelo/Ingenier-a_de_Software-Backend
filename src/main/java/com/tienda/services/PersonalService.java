package com.tienda.services;

import com.tienda.models.Personal;
import com.tienda.repositories.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectar PasswordEncoder

    public List<Personal> getAllPersonal() {
        return personalRepository.findAll();
    }

    public Optional<Personal> getPersonalById(Long id) {
        return personalRepository.findById(id);
    }

    public Personal savePersonal(Personal personal) {
        if (personal.getContrasena() != null && !personal.getContrasena().isEmpty()) {
            personal.setContrasena(passwordEncoder.encode(personal.getContrasena()));
        } else {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        return personalRepository.save(personal);
    }

    public void deletePersonal(Long id) {
        personalRepository.deleteById(id);
    }

    // Puedes añadir métodos para actualizar solo ciertos campos, buscar por usuario, etc.
    public Optional<Personal> updatePersonal(Long id, Personal personalDetails) {
        return personalRepository.findById(id).map(personal -> {
            personal.setUsuario(personalDetails.getUsuario());
            personal.setContrasena(personalDetails.getContrasena()); // Cuidado aquí, encriptar en un sistema real
            personal.setRol(personalDetails.getRol());
            return personalRepository.save(personal);
        });
    }
}
