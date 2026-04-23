package com.arcade.backend.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arcade.backend.entidades.CartaCodejack;

@Repository
public interface CartaCodejackRepositorio extends JpaRepository<CartaCodejack, Long> {
}