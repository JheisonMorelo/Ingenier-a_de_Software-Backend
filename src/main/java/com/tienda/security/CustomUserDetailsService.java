package com.tienda.security;

import com.tienda.models.Personal;
import com.tienda.repositories.PersonalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Para definir roles/autoridades
import java.util.Collections; // Para Collections.singletonList

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonalRepository personalRepository;

    @Override
    public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
        Personal personal = personalRepository.findByUsuario(user)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + user));
                
        return new org.springframework.security.core.userdetails.User(
                personal.getUsuario(),
                personal.getContrasena(), // La contraseña debe estar encriptada en la BD
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + personal.getRol())) // Asigna el rol con prefijo "ROLE_"
        );
    }
}
