package com.example.gestionvehiculos.repository;

import com.example.gestionvehiculos.entity.Usuario;
import com.example.gestionvehiculos.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUserName(String userName);
    
    List<Usuario> findByRol(Rol rol);
    
    List<Usuario> findByActivo(boolean activo);
    
    boolean existsByUserName(String userName);
}
