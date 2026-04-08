package com.arcade.backend.controladores;

import com.arcade.backend.entidades.Pregunta;
import com.arcade.backend.repositorios.PreguntaRepositorio;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/preguntas")
@CrossOrigin(origins = "*")
public class PreguntaControlador {

    private final PreguntaRepositorio preguntaRepositorio;

    public PreguntaControlador(PreguntaRepositorio preguntaRepositorio) {
        this.preguntaRepositorio = preguntaRepositorio;
    }

    @GetMapping("/{idPareja}")
    public Pregunta obtenerPregunta(@PathVariable Integer idPareja) {
        List<Pregunta> preguntas = preguntaRepositorio.findByIdPareja(idPareja);
        
        if (preguntas.isEmpty()) {
            return null; 
        }
        
        // Elegimos una al azar de la lista
        Random random = new Random();
        return preguntas.get(random.nextInt(preguntas.size()));
    }
}