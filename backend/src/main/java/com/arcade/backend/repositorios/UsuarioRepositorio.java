package com.arcade.backend.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arcade.backend.entidades.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    
    Optional<Usuario> findByCorreo(String correo);
    
  
    Optional<Usuario> findByAlias(String alias);
}