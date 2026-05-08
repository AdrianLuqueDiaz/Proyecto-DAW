package com.arcade.backend.controladores;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcade.backend.entidades.LogicLink;
import com.arcade.backend.repositorios.LogicLinkRepositorio;

@RestController
@RequestMapping("/api/logiclink")
@CrossOrigin(origins = "http://localhost:5173")
public class LogicLinkControlador {

    private final LogicLinkRepositorio logicLinkRepositorio;

    public LogicLinkControlador(LogicLinkRepositorio logicLinkRepositorio) {
        this.logicLinkRepositorio = logicLinkRepositorio;
    }

    @GetMapping
    public List<LogicLink> obtenerTodos() {
        return logicLinkRepositorio.findAll();
    }
}