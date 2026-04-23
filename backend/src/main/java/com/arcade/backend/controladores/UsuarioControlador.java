package com.arcade.backend.controladores;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // --- NUEVO: ENDPOINT PARA LA CLASIFICACIÓN ---
    @GetMapping("/clasificacion")
    public List<Usuario> obtenerClasificacion() {
        // Obtenemos todos y los ordenamos de mayor a menor por saldo de Bytes
        return usuarioRepositorio.findAll().stream()
                .sorted((u1, u2) -> u2.getSaldoBytes().compareTo(u1.getSaldoBytes()))
                .collect(Collectors.toList());
    }

    // --- NUEVO: ENDPOINT PARA ACTUALIZAR BYTES Y PARTIDAS DESDE LOS JUEGOS ---
    @PutMapping("/{alias}/bytes")
    public ResponseEntity<?> actualizarBytes(@PathVariable String alias, @RequestParam int cantidad) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByAlias(alias);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Actualizamos los bytes
            int nuevoSaldo = usuario.getSaldoBytes() + cantidad;
            usuario.setSaldoBytes(nuevoSaldo);
            
            // Sumamos 1 a las partidas jugadas
            usuario.setPartidasJugadas(usuario.getPartidasJugadas() + 1);
            
            usuarioRepositorio.save(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
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