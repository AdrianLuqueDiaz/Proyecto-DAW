package com.arcade.backend.repositorios;

import com.arcade.backend.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // Aquí podemos añadir búsquedas personalizadas
    // Por ejemplo, para el Login necesitaremos buscar por correo:
    Optional<Usuario> findByCorreo(String correo);
    
    // O buscar por alias para comprobar si ya existe uno igual
    Optional<Usuario> findByAlias(String alias);
}