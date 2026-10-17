package com.michicorp.postulantes.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DominioTest {

    private static final DatosPostulante VALIDO =
            new DatosPostulante("0102030400", "María José Ordóñez", "mj.ordonez@correo.ec", "Analista de datos", 3);

    @ParameterizedTest
    @ValueSource(strings = {"0102030400", "1710034065", "0926687856", "3050000003"})
    void aceptaCedulasValidas(String cedula) {
        assertTrue(Cedula.esValida(cedula));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0102030406", "2510034065", "0162030405", "010203040", "01020304AB", ""})
    void rechazaCedulasInvalidas(String cedula) {
        assertFalse(Cedula.esValida(cedula));
    }

    @Test
    void unaPostulacionCompletaNoTieneErrores() {
        assertEquals(Set.of(), VALIDO.errores().keySet());
    }

    @Test
    void marcaCadaCampoInvalido() {
        var malo = new DatosPostulante("123", "J4", "sin-arroba", "Gerente", 51);
        assertEquals(Set.of("cedula", "nombres", "correo", "puesto", "experiencia"), malo.errores().keySet());
        assertTrue(new DatosPostulante(null, null, null, null, null).normalizado().errores().containsKey("experiencia"));
    }

    @Test
    void normalizaEspaciosYCorreo() {
        var datos = new DatosPostulante(" 0102030400 ", "  María   José ", " MJ@Correo.EC ", "Analista de datos", 1).normalizado();
        assertEquals("0102030400", datos.cedula());
        assertEquals("María José", datos.nombres());
        assertEquals("mj@correo.ec", datos.correo());
    }

    @Test
    void contratadoYDescartadoSonFinales() {
        assertTrue(Estado.POSTULADO.puedePasarA(Estado.ENTREVISTA));
        assertFalse(Estado.POSTULADO.puedePasarA(Estado.CONTRATADO));
        assertTrue(Estado.ENTREVISTA.puedePasarA(Estado.CONTRATADO));
        assertEquals(Set.of(), Estado.CONTRATADO.siguientes());
        assertEquals(Set.of(), Estado.DESCARTADO.siguientes());
    }
}
