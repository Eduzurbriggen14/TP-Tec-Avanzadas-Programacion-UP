package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.AuthResponse;
import com.example.gestionvehiculos.dto.LoginRequest;
import com.example.gestionvehiculos.dto.usuario.UsuarioCreateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioResponseDTO;
import com.example.gestionvehiculos.entity.Usuario;
import com.example.gestionvehiculos.repository.UsuarioRepository;
import com.example.gestionvehiculos.security.JwtTokenProvider;
import com.example.gestionvehiculos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        loginRequest.getPassword()
                )
        );

        // Obtener detalles del usuario
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // Generar token JWT
        String token = jwtTokenProvider.generateToken(userDetails);

        // Obtener información adicional del usuario
        Usuario usuario = usuarioRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear respuesta
        AuthResponse response = new AuthResponse(
                token,
                usuario.getUserName(),
                usuario.getRol(),
                usuario.getNombre(),
                usuario.getApellido()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        // Guardar la contraseña sin encriptar temporalmente para autenticación posterior
        String rawPassword = usuarioCreateDTO.getPasswd();
        
        // Crear usuario (el servicio se encarga de encriptar la contraseña)
        UsuarioResponseDTO nuevoUsuario = usuarioService.crear(usuarioCreateDTO);

        // Autenticar automáticamente después del registro
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        nuevoUsuario.getUserName(),
                        rawPassword // Usar la contraseña sin encriptar para autenticar
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);

        // Crear respuesta
        AuthResponse response = new AuthResponse(
                token,
                nuevoUsuario.getUserName(),
                nuevoUsuario.getRol(),
                nuevoUsuario.getNombre(),
                nuevoUsuario.getApellido()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token", description = "Valida si un token JWT es válido")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtTokenProvider.extractUsername(token);
            
            if (username != null) {
                return ResponseEntity.ok("Token válido para el usuario: " + username);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
    }
}
