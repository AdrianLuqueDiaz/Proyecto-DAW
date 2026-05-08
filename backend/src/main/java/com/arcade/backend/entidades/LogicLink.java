package com.arcade.backend.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logiclink")
public class LogicLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_anterior")
    private String codigoAnterior;

    @Column(name = "codigo_posterior")
    private String codigoPosterior;

    private String respuestaCorrecta;
    private String opcionFalsa1;
    private String opcionFalsa2;

    public LogicLink() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    public String getCodigoPosterior() {
        return codigoPosterior;
    }

    public void setCodigoPosterior(String codigoPosterior) {
        this.codigoPosterior = codigoPosterior;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getOpcionFalsa1() {
        return opcionFalsa1;
    }

    public void setOpcionFalsa1(String opcionFalsa1) {
        this.opcionFalsa1 = opcionFalsa1;
    }

    public String getOpcionFalsa2() {
        return opcionFalsa2;
    }

    public void setOpcionFalsa2(String opcionFalsa2) {
        this.opcionFalsa2 = opcionFalsa2;
    }
}