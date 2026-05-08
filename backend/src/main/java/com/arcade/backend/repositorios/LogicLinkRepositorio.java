package com.arcade.backend.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.arcade.backend.entidades.LogicLink;

@Repository
public interface LogicLinkRepositorio extends JpaRepository<LogicLink, Long> {
}