package com.arcade.backend.controladores;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arcade.backend.entidades.EstadisticaJuego;
import com.arcade.backend.repositorios.EstadisticaJuegoRepositorio;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "http://localhost:5173")
public class EstadisticaControlador {

    @Autowired
    private EstadisticaJuegoRepositorio estadisticaRepositorio;

    @GetMapping
    public List<EstadisticaJuego> obtenerTodas() {
        return estadisticaRepositorio.findAll();
    }

    @PutMapping("/{nombreJuego}/registrar")
    public ResponseEntity<EstadisticaJuego> registrarPartida(
            @PathVariable String nombreJuego,
            @RequestParam Long bytesCambiados) {
        
        Optional<EstadisticaJuego> optEstadistica = estadisticaRepositorio.findByNombreJuego(nombreJuego);
        
        if (optEstadistica.isPresent()) {
            EstadisticaJuego est = optEstadistica.get();
            est.setPartidasJugadas(est.getPartidasJugadas() + 1);
            est.setBytesRepartidos(est.getBytesRepartidos() + bytesCambiados);
            estadisticaRepositorio.save(est);
            return ResponseEntity.ok(est);
        }
        
        return ResponseEntity.notFound().build();
    }
}