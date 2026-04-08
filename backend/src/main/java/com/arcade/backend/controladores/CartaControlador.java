package com.arcade.backend.controladores;

import com.arcade.backend.entidades.Carta;
import com.arcade.backend.repositorios.CartaRepositorio;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartas")
@CrossOrigin(origins = "*")
public class CartaControlador {

    private final CartaRepositorio cartaRepositorio;

    public CartaControlador(CartaRepositorio cartaRepositorio) {
        this.cartaRepositorio = cartaRepositorio;
    }

    @GetMapping
    public List<Carta> obtenerTodas() {
        return cartaRepositorio.findAll();
    }
}