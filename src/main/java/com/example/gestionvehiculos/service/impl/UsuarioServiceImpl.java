package com.example.gestionvehiculos.service.impl;

import com.example.gestionvehiculos.dto.UsuarioDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioCreateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioUpdateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioResponseDTO;
import com.example.gestionvehiculos.entity.Usuario;
import com.example.gestionvehiculos.enums.Rol;
import com.example.gestionvehiculos.exception.DuplicateResourceException;
import com.example.gestionvehiculos.exception.ResourceNotFoundException;
import com.example.gestionvehiculos.repository.UsuarioRepository;
import com.example.gestionvehiculos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO crear(UsuarioCreateDTO createDTO) {
        if (usuarioRepository.existsByUserName(createDTO.getUserName())) {
            throw new DuplicateResourceException("El nombre de usuario ya existe: " + createDTO.getUserName());
        }

        Usuario usuario = new Usuario();
        usuario.setDni(createDTO.getDni());
        usuario.setNombre(createDTO.getNombre());
        usuario.setApellido(createDTO.getApellido());
        usuario.setCorreo(createDTO.getCorreo());
        usuario.setTelefono(createDTO.getTelefono());
        usuario.setUserName(createDTO.getUserName());
        usuario.setPasswd(passwordEncoder.encode(createDTO.getPasswd()));
        usuario.setRol(createDTO.getRol());
        usuario.setActivo(true);
        
        Usuario guardado = usuarioRepository.save(usuario);
        
        return convertirEntidadaResponse(guardado);
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO updateDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        // Actualizar solo los campos proporcionados
        if (updateDTO.getNombre() != null) {
            usuario.setNombre(updateDTO.getNombre());
        }
        if (updateDTO.getApellido() != null) {
            usuario.setApellido(updateDTO.getApellido());
        }
        if (updateDTO.getCorreo() != null) {
            usuario.setCorreo(updateDTO.getCorreo());
        }
        if (updateDTO.getTelefono() != null) {
            usuario.setTelefono(updateDTO.getTelefono());
        }
        
        // Actualizar contraseña solo si se proporciona
        if (updateDTO.getPasswd() != null && !updateDTO.getPasswd().isEmpty()) {
            usuario.setPasswd(passwordEncoder.encode(updateDTO.getPasswd()));
        }
        
        if (updateDTO.getActivo() != null) {
            usuario.setActivo(updateDTO.getActivo());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirEntidadaResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return convertirEntidadaResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirEntidadaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorUserName(String userName) {
        Usuario usuario = usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userName));
        return convertirEntidadaResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirEntidadaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerPorActivo(boolean activo) {
        return usuarioRepository.findByActivo(activo).stream()
                .map(this::convertirEntidadaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean autenticar(String userName, String password) {
        Usuario usuario = usuarioRepository.findByUserName(userName)
                .orElse(null);
        return usuario != null && usuario.isActivo() && passwordEncoder.matches(password, usuario.getPasswd());
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO convertirEntidadaResponse(Usuario usuario) {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setCorreo(usuario.getCorreo());
        response.setTelefono(usuario.getTelefono());
        response.setUserName(usuario.getUserName());
        // NO incluir password por seguridad
        response.setRol(usuario.getRol());
        response.setActivo(usuario.isActivo());
        return response;
    }
}
