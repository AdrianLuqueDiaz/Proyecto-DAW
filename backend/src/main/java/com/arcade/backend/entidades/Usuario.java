package com.arcade.backend.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String alias;

    @Email(message = "El formato del correo no es válido")
    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column(name = "saldo_bytes")
    private Integer saldoBytes = 1000; 

    @Column(name = "partidas_jugadas")
    private Integer partidasJugadas = 0;

    private String rol = "USUARIO";

    public Usuario() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Integer getSaldoBytes() { return saldoBytes; }
    public void setSaldoBytes(Integer saldoBytes) { this.saldoBytes = saldoBytes; }

    public Integer getPartidasJugadas() { return partidasJugadas; }
    public void setPartidasJugadas(Integer partidasJugadas) { this.partidasJugadas = partidasJugadas; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}