package com.example.gestionvehiculos.service;

import com.example.gestionvehiculos.dto.usuario.UsuarioCreateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioUpdateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioResponseDTO;
import com.example.gestionvehiculos.entity.Usuario;
import com.example.gestionvehiculos.enums.Rol;
import com.example.gestionvehiculos.exception.DuplicateResourceException;
import com.example.gestionvehiculos.exception.InvalidOperationException;
import com.example.gestionvehiculos.exception.ResourceNotFoundException;
import com.example.gestionvehiculos.repository.UsuarioRepository;
import com.example.gestionvehiculos.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Servicio de Usuarios")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioCreateDTO usuarioCreateDTO;
    private UsuarioUpdateDTO usuarioUpdateDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioCreateDTO = new UsuarioCreateDTO();
        usuarioCreateDTO.setUserName("admin");
        usuarioCreateDTO.setPasswd("password123");
        usuarioCreateDTO.setNombre("Admin");
        usuarioCreateDTO.setApellido("Sistema");
        usuarioCreateDTO.setCorreo("admin@example.com");
        usuarioCreateDTO.setTelefono("1234567890");
        usuarioCreateDTO.setRol(Rol.ADMIN);

        usuarioUpdateDTO = new UsuarioUpdateDTO();
        usuarioUpdateDTO.setNombre("Admin Updated");
        usuarioUpdateDTO.setApellido("Sistema");
        usuarioUpdateDTO.setCorreo("admin@example.com");
        usuarioUpdateDTO.setTelefono("1234567890");
        usuarioUpdateDTO.setPasswd("newPassword456");
        usuarioUpdateDTO.setActivo(true);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUserName("admin");
        usuario.setPasswd("$2a$10$encodedPassword");
        usuario.setNombre("Admin");
        usuario.setApellido("Sistema");
        usuario.setCorreo("admin@example.com");
        usuario.setTelefono("1234567890");
        usuario.setRol(Rol.ADMIN);
        usuario.setActivo(true);
    }

    @Test
    @DisplayName("Crear usuario exitosamente con password encriptado")
    void testCrearUsuario_Success() {
        // Arrange
        when(usuarioRepository.existsByUserName("admin")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioResponseDTO resultado = usuarioService.crear(usuarioCreateDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("admin", resultado.getUserName());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Error al crear usuario con username duplicado")
    void testCrearUsuario_UsernameDuplicado() {
        // Arrange
        when(usuarioRepository.existsByUserName("admin")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            usuarioService.crear(usuarioCreateDTO);
        });
        
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Autenticar usuario con credenciales correctas")
    void testAutenticarUsuario_Success() {
        // Arrange
        when(usuarioRepository.findByUserName("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);

        // Act
        boolean resultado = usuarioService.autenticar("admin", "password123");

        // Assert
        assertTrue(resultado);
        verify(passwordEncoder, times(1)).matches("password123", "$2a$10$encodedPassword");
    }

    @Test
    @DisplayName("Error al autenticar con credenciales incorrectas")
    void testAutenticarUsuario_CredencialesIncorrectas() {
        // Arrange
        when(usuarioRepository.findByUserName("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "$2a$10$encodedPassword")).thenReturn(false);

        // Act
        boolean resultado = usuarioService.autenticar("admin", "wrongPassword");

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Error al autenticar usuario inactivo")
    void testAutenticarUsuario_UsuarioInactivo() {
        // Arrange
        usuario.setActivo(false);
        when(usuarioRepository.findByUserName("admin")).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = usuarioService.autenticar("admin", "password123");

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Obtener usuario por username")
    void testObtenerPorUserName_Success() {
        // Arrange
        when(usuarioRepository.findByUserName("admin")).thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponseDTO resultado = usuarioService.obtenerPorUserName("admin");

        // Assert
        assertNotNull(resultado);
        assertEquals("admin", resultado.getUserName());
        assertEquals(Rol.ADMIN, resultado.getRol());
    }

    @Test
    @DisplayName("Obtener usuarios por rol")
    void testObtenerPorRol() {
        // Arrange
        Usuario usuario2 = new Usuario();
        usuario2.setUserName("admin2");
        usuario2.setRol(Rol.ADMIN);
        
        when(usuarioRepository.findByRol(Rol.ADMIN))
            .thenReturn(Arrays.asList(usuario, usuario2));

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.obtenerPorRol(Rol.ADMIN);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(u -> u.getRol() == Rol.ADMIN));
    }

    @Test
    @DisplayName("Cambiar estado de usuario")
    void testCambiarEstado() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.cambiarEstado(1L, false);

        // Assert
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Actualizar usuario manteniendo password encriptado")
    void testActualizarUsuario_Success() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPassword456")).thenReturn("$2a$10$newEncodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioResponseDTO resultado = usuarioService.actualizar(1L, usuarioUpdateDTO);

        // Assert
        assertNotNull(resultado);
        verify(passwordEncoder, times(1)).encode("newPassword456");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Eliminar usuario exitosamente")
    void testEliminar_Success() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        // Act
        usuarioService.eliminar(1L);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(1L);
    }
}
