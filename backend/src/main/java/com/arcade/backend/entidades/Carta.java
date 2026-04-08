package com.arcade.backend.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "cartas_memory")
public class Carta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texto;

    @Column(name = "id_pareja")
    private Integer idPareja;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Integer getIdPareja() { return idPareja; }
    public void setIdPareja(Integer idPareja) { this.idPareja = idPareja; }
}