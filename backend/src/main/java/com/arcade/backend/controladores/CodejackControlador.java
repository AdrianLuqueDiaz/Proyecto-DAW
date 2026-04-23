package com.arcade.backend.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcade.backend.entidades.CartaCodejack;
import com.arcade.backend.repositorios.CartaCodejackRepositorio;

@RestController
@RequestMapping("/api/codejack")
@CrossOrigin(origins = "*") 
public class CodejackControlador {

    @Autowired
    private CartaCodejackRepositorio cartaRepositorio;

    @GetMapping("/cartas")
    public List<CartaCodejack> obtenerBaraja() {
       
        return cartaRepositorio.findAll();
    }
}