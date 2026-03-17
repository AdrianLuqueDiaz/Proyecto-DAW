package com.arcade.backend.controladores;

import com.arcade.backend.entidades.Usuario;
import com.arcade.backend.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Obtener todos los usuarios (para probar)
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioRepositorio.findAll();
    }

    // Registrar un nuevo usuario
    @PostMapping("/registro")
    public Usuario registrar(@RequestBody Usuario nuevoUsuario) {
        // Por ahora lo guardamos tal cual (más adelante encriptaremos la contraseña)
        return usuarioRepositorio.save(nuevoUsuario);
    }
    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario datosLogin) {
        // Buscamos al usuario por su alias
        return usuarioRepositorio.findByAlias(datosLogin.getAlias())
                .filter(u -> u.getContrasena().equals(datosLogin.getContrasena()))
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));
    }
    
}