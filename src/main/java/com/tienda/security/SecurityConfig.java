package com.tienda.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.dto.AuthResponse;
import com.tienda.dto.MessageResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Para seguridad a nivel de método
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Para encriptar contraseñas
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import jakarta.servlet.http.HttpServletResponse;

@Configuration // Indica que esta clase contiene configuraciones para Spring
@EnableWebSecurity // Habilita la seguridad web de Spring
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) // Habilita seguridad a nivel de método (ej. @Secured, @RolesAllowed)
@EnableJdbcHttpSession
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper; 

    // Define un bean para el PasswordEncoder (BCrypt es recomendado)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura el AuthenticationManager para usar nuestro CustomUserDetailsService y el PasswordEncoder
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configura el DaoAuthenticationProvider para usar nuestro UserDetailsService y PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Configura la cadena de filtros de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST (en producción, considerar CSRF Tokens)
            .cors(cors -> cors.disable()) // Deshabilitar CORS para simplificar (ya lo manejas con @CrossOrigin en controladores)
                                         // Si no tuvieras @CrossOrigin, necesitarías configurarlo aquí
            .authorizeHttpRequests(authorize -> authorize
                // Permitir acceso sin autenticación a la ruta de login
                .requestMatchers("/api/auth/**").permitAll()
                // Reglas de autorización basadas en roles
                .requestMatchers("/api/personal/**").hasRole("ADMIN") // Solo ADMIN puede gestionar Personal
                .requestMatchers("/api/gastos/**").hasRole("ADMIN")   // Solo ADMIN puede gestionar Gastos
                .requestMatchers("/api/proveedores/**").hasRole("ADMIN") // Solo ADMIN puede gestionar Proveedores
                .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "VENDEDOR") // ADMIN y VENDEDOR pueden ver/gestionar Productos
                .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "VENDEDOR") // ADMIN y VENDEDOR pueden gestionar Ventas
                .requestMatchers("/api/clientes-pago-pendiente/**").hasAnyRole("ADMIN", "VENDEDOR") // ADMIN y VENDEDOR pueden gestionar Clientes Fiados
                .requestMatchers("/api/fiados/**").hasAnyRole("ADMIN", "VENDEDOR") // ADMIN y VENDEDOR pueden gestionar Fiados y Abonos
                // Todas las demás solicitudes requieren autenticación
                .anyRequest().authenticated()
            )
            // *** NUEVA CONFIGURACIÓN PARA MANEJAR EXCEPCIONES DE AUTENTICACIÓN (401) ***
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    // Este código se ejecuta cuando un usuario NO AUTENTICADO intenta acceder a un recurso protegido
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Establece el código de estado 401
                    response.setContentType("application/json"); // Configura el tipo de contenido
                    response.setCharacterEncoding("UTF-8"); // Configura la codificación
                    // Escribe la respuesta JSON
                    response.getWriter().write(objectMapper.writeValueAsString(new MessageResponse("Acceso no autorizado. Por favor, inicie sesión.")));
                })
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login") // URL donde el frontend enviará POST para login
                .successHandler((request, response, authentication) -> {

                    String username = authentication.getName();
                    List<String> roles = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(role -> role.replace("ROLE_", ""))
                            .collect(Collectors.toList());

                    // Se crea una instancia de AuthResponse con los datos del usuario autenticado
                    // y se serializa a JSON para la respuesta HTTP 200 OK.
                    AuthResponse authResponse = new AuthResponse("Login exitoso", username, roles);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(authResponse));
                })
                // Manejador de fallo inline
                .failureHandler((request, response, exception) -> {
                    String errorMessage = "Credenciales inválidas."; 
                    MessageResponse errorResponse = new MessageResponse(errorMessage);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                }).permitAll() // Permitir acceso a la URL de procesamiento de login
            )
            // Configuración de logout
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler((request, response, authentication) -> { // <--- NUEVO LogoutSuccessHandler
                    // Este handler se ejecuta DESPUÉS de un logout exitoso (sesión invalidada)
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(objectMapper.writeValueAsString(new MessageResponse("Sesión cerrada exitosamente.")));
                })
                .permitAll() // Asegura que el endpoint /api/auth/logout sea accesible sin autenticación
            );

        // Añade el proveedor de autenticación
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}
