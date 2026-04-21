package com.arcade.backend.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcade.backend.entidades.Usuario;
import com.arcade.backend.repositorios.UsuarioRepositorio;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioRepositorio.findAll();
    }

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody Usuario nuevoUsuario) {
        
        String contrasenaCifrada = passwordEncoder.encode(nuevoUsuario.getContrasena());
        nuevoUsuario.setContrasena(contrasenaCifrada);
        
        return usuarioRepositorio.save(nuevoUsuario);
    }
    
    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario datosLogin) {
        
        return usuarioRepositorio.findByAlias(datosLogin.getAlias())
                .filter(u -> passwordEncoder.matches(datosLogin.getContrasena(), u.getContrasena()))
                .orElseThrow(() -> new RuntimeException("Usuario o contraseña incorrectos"));
    }
}