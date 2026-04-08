package com.arcade.backend.repositorios;

import com.arcade.backend.entidades.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepositorio extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByIdPareja(Integer idPareja);
}