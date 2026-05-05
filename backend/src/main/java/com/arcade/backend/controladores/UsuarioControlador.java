package com.arcade.backend.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid; // Importación necesaria para la validación

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

    @GetMapping("/clasificacion")
    public List<Usuario> obtenerClasificacion() {
        List<Usuario> lista = usuarioRepositorio.findAll();
        
        lista.sort((u1, u2) -> u2.getSaldoBytes().compareTo(u1.getSaldoBytes()));
        
        return lista;
    }

    @PutMapping("/{alias}/bytes")
    public ResponseEntity<Usuario> actualizarBytes(@PathVariable String alias, @RequestParam int cantidad) {
        Usuario usuario = usuarioRepositorio.findByAlias(alias).orElse(null);
        
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        usuario.setSaldoBytes(usuario.getSaldoBytes() + cantidad);
        
        // Suma 1 cada partida jugada
        usuario.setPartidasJugadas(usuario.getPartidasJugadas() + 1);
        
        usuarioRepositorio.save(usuario);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registro")
    public Usuario registrar(@Valid @RequestBody Usuario nuevoUsuario) { // <-- Se añade @Valid aquí
        String contrasenaCifrada = passwordEncoder.encode(nuevoUsuario.getContrasena());
        nuevoUsuario.setContrasena(contrasenaCifrada);
        
        return usuarioRepositorio.save(nuevoUsuario);
    }
    
    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario datosLogin) {

        Usuario usuario = usuarioRepositorio.findByAlias(datosLogin.getAlias()).orElse(null);

        if (usuario != null && passwordEncoder.matches(datosLogin.getContrasena(), usuario.getContrasena())) {
            return usuario;
        }
        
        throw new RuntimeException("Usuario o contraseña incorrectos");
    }
}