package com.arcade.backend.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estadisticas_juegos")
public class EstadisticaJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_juego", unique = true, nullable = false)
    private String nombreJuego;

    @Column(name = "partidas_jugadas")
    private Long partidasJugadas = 0L;

    @Column(name = "bytes_repartidos")
    private Long bytesRepartidos = 0L;

    public EstadisticaJuego() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreJuego() { return nombreJuego; }
    public void setNombreJuego(String nombreJuego) { this.nombreJuego = nombreJuego; }

    public Long getPartidasJugadas() { return partidasJugadas; }
    public void setPartidasJugadas(Long partidasJugadas) { this.partidasJugadas = partidasJugadas; }

    public Long getBytesRepartidos() { return bytesRepartidos; }
    public void setBytesRepartidos(Long bytesRepartidos) { this.bytesRepartidos = bytesRepartidos; }
}