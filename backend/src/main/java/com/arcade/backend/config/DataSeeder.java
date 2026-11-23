package com.arcade.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.arcade.backend.entidades.Carta;
import com.arcade.backend.entidades.CartaCodejack;
import com.arcade.backend.entidades.EstadisticaJuego;
import com.arcade.backend.entidades.LogicLink;
import com.arcade.backend.entidades.Pregunta;
import com.arcade.backend.entidades.Usuario;
import com.arcade.backend.repositorios.CartaCodejackRepositorio;
import com.arcade.backend.repositorios.CartaRepositorio;
import com.arcade.backend.repositorios.EstadisticaJuegoRepositorio;
import com.arcade.backend.repositorios.LogicLinkRepositorio;
import com.arcade.backend.repositorios.PreguntaRepositorio;
import com.arcade.backend.repositorios.UsuarioRepositorio;

/**
 * Carga datos iniciales la primera vez que arranca el backend con una base de datos vacia.
 * Si una tabla ya tiene datos, no la toca. El usuario administrador solo se crea si existe
 * la variable de entorno ADMIN_PASSWORD (la contrasena nunca se guarda en el codigo).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CartaRepositorio cartas;
    private final PreguntaRepositorio preguntas;
    private final CartaCodejackRepositorio codejack;
    private final LogicLinkRepositorio logicLink;
    private final EstadisticaJuegoRepositorio estadisticas;
    private final UsuarioRepositorio usuarios;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CartaRepositorio cartas, PreguntaRepositorio preguntas, CartaCodejackRepositorio codejack,
                      LogicLinkRepositorio logicLink, EstadisticaJuegoRepositorio estadisticas,
                      UsuarioRepositorio usuarios, PasswordEncoder passwordEncoder) {
        this.cartas = cartas;
        this.preguntas = preguntas;
        this.codejack = codejack;
        this.logicLink = logicLink;
        this.estadisticas = estadisticas;
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        cargarMemory();
        cargarCodejack();
        cargarLogicLink();
        cargarEstadisticas();
        crearAdmin();
        crearInvitado();
    }

    // ---------- Logic Slots (memory): 8 parejas de cartas + 1 pregunta por pareja ----------
    private void cargarMemory() {
        if (cartas.count() == 0) {
            String[][] parejas = {
                {"HTML", "Estructura"}, {"CSS", "Estilos"}, {"JavaScript", "Interacción"}, {"SQL", "Consultas"},
                {"Git", "Versiones"}, {"Java", "POO"}, {"HTTP", "Protocolo"}, {"JSON", "Datos"}
            };
            for (int i = 0; i < parejas.length; i++) {
                for (String texto : parejas[i]) {
                    Carta c = new Carta();
                    c.setTexto(texto);
                    c.setIdPareja(i + 1);
                    cartas.save(c);
                }
            }
        }
        if (preguntas.count() == 0) {
            String[][] p = {
                {"¿Qué etiqueta HTML define el enlace a otra página?", "<a>", "<link>", "<href>", "<url>"},
                {"¿Qué propiedad CSS cambia el color del texto?", "color", "font-color", "text-color", "background"},
                {"¿Qué método añade un elemento al final de un array en JavaScript?", "push()", "add()", "append()", "insert()"},
                {"¿Qué cláusula SQL filtra las filas de una consulta?", "WHERE", "ORDER BY", "GROUP BY", "FROM"},
                {"¿Qué comando de Git sube los commits al repositorio remoto?", "git push", "git commit", "git pull", "git add"},
                {"¿Qué palabra clave de Java crea una clase hija?", "extends", "implements", "inherits", "super"},
                {"¿Qué código de estado HTTP significa «no encontrado»?", "404", "200", "500", "301"},
                {"¿Qué formato usa pares clave-valor entre llaves?", "JSON", "CSV", "XML", "YAML"}
            };
            for (int i = 0; i < p.length; i++) {
                Pregunta q = new Pregunta();
                q.setIdPareja(i + 1);
                q.setEnunciado(p[i][0]);
                q.setRespuestaCorrecta(p[i][1]);
                q.setFalsa1(p[i][2]);
                q.setFalsa2(p[i][3]);
                q.setFalsa3(p[i][4]);
                preguntas.save(q);
            }
        }
    }

    // ---------- Code-Jack 21: cartas con un valor numérico ----------
    private void cargarCodejack() {
        if (codejack.count() > 0) return;
        Object[][] baraja = {
            {"int x = 1;", 1.0}, {"return 2;", 2.0}, {"i += 3;", 3.0}, {"for(4)", 4.0}, {"while(5)", 5.0},
            {"new Array(6)", 6.0}, {"switch(7)", 7.0}, {"byte b = 8;", 8.0}, {"x * 9", 9.0},
            {"if/else", 10.0}, {"try/catch", 10.0}, {"class {}", 10.0}, {"main()", 11.0}
        };
        for (Object[] c : baraja) {
            codejack.save(new CartaCodejack((String) c[0], (Double) c[1]));
        }
    }

    // ---------- Logic Link: completar la línea que falta ----------
    private void cargarLogicLink() {
        if (logicLink.count() > 0) return;
        String[][] retos = {
            {"for (int i = 0; i < 5; i++) {", "}", "System.out.println(i);", "return i;", "break i;"},
            {"if (edad >= 18) {", "} else { menor(); }", "mayor();", "edad = 18;", "continue;"},
            {"const lista = [1, 2, 3];", "console.log(total);", "const total = lista.length;", "lista = 3;", "total == lista;"},
            {"SELECT nombre", "WHERE edad > 18;", "FROM usuarios", "INTO usuarios", "VALUES (18)"},
            {"public int sumar(int a, int b) {", "}", "return a + b;", "print(a + b);", "a + b = c;"},
            {"git add .", "git push", "git commit -m \"cambios\"", "git clone", "git init"}
        };
        for (String[] r : retos) {
            LogicLink l = new LogicLink();
            l.setCodigoAnterior(r[0]);
            l.setCodigoPosterior(r[1]);
            l.setRespuestaCorrecta(r[2]);
            l.setOpcionFalsa1(r[3]);
            l.setOpcionFalsa2(r[4]);
            logicLink.save(l);
        }
    }

    // ---------- Estadísticas: una fila por juego (los nombres que usa el frontend) ----------
    private void cargarEstadisticas() {
        for (String juego : new String[] {"Logic Slots", "Code-Jack 21", "Logic Link", "Decibit"}) {
            if (estadisticas.findByNombreJuego(juego).isEmpty()) {
                EstadisticaJuego e = new EstadisticaJuego();
                e.setNombreJuego(juego);
                estadisticas.save(e);
            }
        }
    }

    // ---------- Administrador: su contraseña se sincroniza con ADMIN_PASSWORD en cada arranque ----------
    private void crearAdmin() {
        String pass = System.getenv("ADMIN_PASSWORD");
        if (pass == null || pass.isBlank()) return;
        pass = pass.strip();
        Usuario admin = usuarios.findByAlias("admin").orElseGet(Usuario::new);
        if (admin.getId() != null && passwordEncoder.matches(pass, admin.getContrasena())
                && "ADMIN".equals(admin.getRol())) {
            return; // ya está al día
        }
        admin.setAlias("admin");
        if (admin.getCorreo() == null) admin.setCorreo("admin@bithub.local");
        admin.setContrasena(passwordEncoder.encode(pass));
        admin.setRol("ADMIN");
        usuarios.save(admin);
    }

    // ---------- Usuario de demo público (invitado / invitado) para quien quiera probar la web ----------
    private void crearInvitado() {
        Usuario invitado = usuarios.findByAlias("invitado").orElseGet(Usuario::new);
        if (invitado.getId() != null && passwordEncoder.matches("invitado", invitado.getContrasena())) {
            return;
        }
        invitado.setAlias("invitado");
        if (invitado.getCorreo() == null) invitado.setCorreo("invitado@bithub.local");
        invitado.setContrasena(passwordEncoder.encode("invitado"));
        invitado.setRol("USUARIO");
        usuarios.save(invitado);
    }
}
