package com.arcade.backend.repositorios;

import com.arcade.backend.entidades.Carta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaRepositorio extends JpaRepository<Carta, Long> {
}