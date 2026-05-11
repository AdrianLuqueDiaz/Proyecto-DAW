package com.arcade.backend.controladores;

import java.util.List;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arcade.backend.entidades.Pregunta;
import com.arcade.backend.repositorios.PreguntaRepositorio;

@RestController
@RequestMapping("/api/preguntas")
@CrossOrigin(origins = {"http://localhost:5173", "https://bithub-arcade.vercel.app"})
public class PreguntaControlador {

    private final PreguntaRepositorio preguntaRepositorio;

    public PreguntaControlador(PreguntaRepositorio preguntaRepositorio) {
        this.preguntaRepositorio = preguntaRepositorio;
    }

    @GetMapping
    public List<Pregunta> obtenerTodas() {
        return preguntaRepositorio.findAll();
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

    @PostMapping("/add")
    public Pregunta crearPregunta(@RequestBody Pregunta nuevaPregunta) {
        return preguntaRepositorio.save(nuevaPregunta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPregunta(@PathVariable Long id) {
        if (preguntaRepositorio.existsById(id)) {
            preguntaRepositorio.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}