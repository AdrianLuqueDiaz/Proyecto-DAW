package com.arcade.backend.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String alias;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasena;

    @Column(name = "saldo_bytes")
    private Integer saldoBytes = 1000; // Empezamos con 1000 bytes

    private String rol = "USUARIO";

    // --- Constructor vacío (Obligatorio para JPA) ---
    public Usuario() {}

    // --- Getters y Setters (Para que otras partes del código lean/escriban datos) ---
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

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}