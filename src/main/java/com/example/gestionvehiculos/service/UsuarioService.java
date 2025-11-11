package com.example.gestionvehiculos.service;

import com.example.gestionvehiculos.dto.usuario.UsuarioCreateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioUpdateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioResponseDTO;
import com.example.gestionvehiculos.enums.Rol;
import java.util.List;

public interface UsuarioService {
    
    UsuarioResponseDTO crear(UsuarioCreateDTO usuarioCreateDTO);
    
    UsuarioResponseDTO actualizar(Long id, UsuarioUpdateDTO usuarioUpdateDTO);
    
    void eliminar(Long id);
    
    UsuarioResponseDTO obtenerPorId(Long id);
    
    List<UsuarioResponseDTO> obtenerTodos();
    
    UsuarioResponseDTO obtenerPorUserName(String userName);
    
    List<UsuarioResponseDTO> obtenerPorRol(Rol rol);
    
    List<UsuarioResponseDTO> obtenerPorActivo(boolean activo);
    
    boolean autenticar(String userName, String password);
    
    void cambiarEstado(Long id, boolean activo);
}
