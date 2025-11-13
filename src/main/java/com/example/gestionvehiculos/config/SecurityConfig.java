package com.example.gestionvehiculos.config;

import com.example.gestionvehiculos.security.JwtAuthenticationEntryPoint;
import com.example.gestionvehiculos.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        // Endpoints de usuarios - solo ADMIN puede crear/eliminar
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("ADMIN", "ADMINISTRATIVO")
                        
                        // Endpoints de clientes - ADMIN y ADMINISTRATIVO
                        .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "ADMINISTRATIVO")
                        
                        // Endpoints de vehículos - todos los autenticados pueden ver, solo ADMIN puede modificar
                        .requestMatchers(HttpMethod.GET, "/api/vehiculos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/vehiculos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/vehiculos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/vehiculos/**").hasRole("ADMIN")
                        
                        // Endpoints de alquileres - ADMIN y ADMINISTRATIVO
                        .requestMatchers("/api/alquileres/**").hasAnyRole("ADMIN", "ADMINISTRATIVO")
                        
                        // Endpoints de revisiones - ADMIN y INSPECTOR
                        .requestMatchers("/api/revisiones/**").hasAnyRole("ADMIN", "INSPECTOR")
                        
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
