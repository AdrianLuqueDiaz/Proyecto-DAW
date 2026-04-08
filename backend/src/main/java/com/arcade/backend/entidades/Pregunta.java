package com.arcade.backend.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "preguntas_arcade")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_pareja")
    private Integer idPareja;

    private String enunciado;

    @Column(name = "respuesta_correcta")
    private String respuestaCorrecta;

    @Column(name = "falsa_1")
    private String falsa1;

    @Column(name = "falsa_2")
    private String falsa2;

    @Column(name = "falsa_3")
    private String falsa3;






    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getIdPareja() { return idPareja; }
    public void setIdPareja(Integer idPareja) { this.idPareja = idPareja; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public String getRespuestaCorrecta() { return respuestaCorrecta; }
    public void setRespuestaCorrecta(String respuestaCorrecta) { this.respuestaCorrecta = respuestaCorrecta; }

    public String getFalsa1() { return falsa1; }
    public void setFalsa1(String falsa1) { this.falsa1 = falsa1; }

    public String getFalsa2() { return falsa2; }
    public void setFalsa2(String falsa2) { this.falsa2 = falsa2; }

    public String getFalsa3() { return falsa3; }
    public void setFalsa3(String falsa3) { this.falsa3 = falsa3; }
}