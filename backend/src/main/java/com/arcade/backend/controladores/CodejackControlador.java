package com.arcade.backend.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcade.backend.entidades.CartaCodejack;
import com.arcade.backend.repositorios.CartaCodejackRepositorio;

@RestController
@RequestMapping("/api/cartas-codejack")
@CrossOrigin(origins = {"http://localhost:5173", "https://bithub-arcade.vercel.app"})
public class CodejackControlador {

    private final CartaCodejackRepositorio cartaRepositorio;

    public CodejackControlador(CartaCodejackRepositorio cartaRepositorio) {
        this.cartaRepositorio = cartaRepositorio;
    }

    @GetMapping
    public List<CartaCodejack> obtenerTodas() {
        return cartaRepositorio.findAll();
    }

    @PostMapping("/add")
    public CartaCodejack crearCarta(@RequestBody CartaCodejack nuevaCarta) {
        return cartaRepositorio.save(nuevaCarta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarta(@PathVariable Long id) {
        if (cartaRepositorio.existsById(id)) {
            cartaRepositorio.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}