package com.arcade.backend.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arcade.backend.entidades.EstadisticaJuego;

@Repository
public interface EstadisticaJuegoRepositorio extends JpaRepository<EstadisticaJuego, Long> {
    Optional<EstadisticaJuego> findByNombreJuego(String nombreJuego);
}