package com.michicorp.postulantes.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.michicorp.postulantes.dominio.DatosPostulante;
import com.michicorp.postulantes.dominio.Estado;
import com.michicorp.postulantes.dominio.Postulante;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostulanteServiceTest {

    private static final ZoneId GYE = ZoneId.of("America/Guayaquil");

    private RepositorioEnMemoria repositorio;
    private PostulanteService servicio;

    private static DatosPostulante datos(String cedula, String nombres, String puesto) {
        return new DatosPostulante(cedula, nombres, nombres.split(" ")[0].toLowerCase() + "@correo.ec", puesto, 2);
    }

    private void fecha(String dia) {
        servicio = new PostulanteService(repositorio,
                Clock.fixed(LocalDate.parse(dia).atStartOfDay(GYE).toInstant(), GYE));
    }

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        servicio = new PostulanteService(repositorio, Clock.fixed(Instant.parse("2026-10-17T15:00:00Z"), GYE));
    }

    @Test
    void crearEmpiezaComoPostuladoConLaFechaDeHoy() {
        Postulante p = servicio.crear(new DatosPostulante(" 0102030400 ", " María  Ordóñez ", "MJ@Correo.EC",
                "Analista de datos", 3));
        assertEquals(1L, p.getId());
        assertEquals(Estado.POSTULADO, p.getEstado());
        assertEquals(LocalDate.of(2026, 10, 17), p.getFechaPostulacion());
        assertEquals("María Ordóñez", p.getNombres());
        assertEquals("mj@correo.ec", p.getCorreo());
    }

    @Test
    void rechazaDatosInvalidosConLosErroresPorCampo() {
        var e = assertThrows(DatosInvalidosException.class,
                () -> servicio.crear(new DatosPostulante("123", "", "x", "Gerente", -1)));
        assertEquals(List.of("cedula", "correo", "experiencia", "nombres", "puesto"),
                e.getErrores().keySet().stream().sorted().toList());
    }

    @Test
    void unaPersonaNoPostulaDosVecesAlMismoPuesto() {
        Postulante p = servicio.crear(datos("0102030400", "María Ordóñez", "Analista de datos"));
        var e = assertThrows(DatosInvalidosException.class,
                () -> servicio.crear(datos("0102030400", "María Ordóñez", "Analista de datos")));
        assertEquals("Esta persona ya se postuló a ese puesto.", e.getErrores().get("cedula"));

        servicio.crear(datos("0102030400", "María Ordóñez", "Soporte técnico"));
        // Editar la propia postulación sin cambiar puesto no choca consigo misma.
        servicio.actualizar(p.getId(), datos("0102030400", "María José Ordóñez", "Analista de datos"));
        assertEquals("María José Ordóñez", servicio.buscar(p.getId()).getNombres());
    }

    @Test
    void elEstadoSoloAvanzaPorLasTransicionesPermitidas() {
        long id = servicio.crear(datos("1710034065", "Luis Pérez", "Desarrollador backend")).getId();
        var e = assertThrows(ConflictoException.class, () -> servicio.cambiarEstado(id, Estado.CONTRATADO));
        assertEquals("No se puede pasar de Postulado a Contratado.", e.getMessage());

        servicio.cambiarEstado(id, Estado.ENTREVISTA);
        servicio.cambiarEstado(id, Estado.CONTRATADO);
        assertThrows(ConflictoException.class, () -> servicio.cambiarEstado(id, Estado.DESCARTADO));
        assertThrows(DatosInvalidosException.class, () -> servicio.cambiarEstado(id, null));
    }

    @Test
    void editarNoCambiaElEstadoNiLaFecha() {
        long id = servicio.crear(datos("1710034065", "Luis Pérez", "Desarrollador backend")).getId();
        servicio.cambiarEstado(id, Estado.ENTREVISTA);
        fecha("2026-11-02");
        Postulante editado = servicio.actualizar(id, datos("1710034065", "Luis Alberto Pérez", "Desarrollador backend"));
        assertEquals(Estado.ENTREVISTA, editado.getEstado());
        assertEquals(LocalDate.of(2026, 10, 17), editado.getFechaPostulacion());
    }

    @Test
    void listaLasMasRecientesPrimeroYFiltra() {
        fecha("2026-10-01");
        servicio.crear(datos("0102030400", "María Ordóñez", "Analista de datos"));
        fecha("2026-10-05");
        long luis = servicio.crear(datos("1710034065", "Luis Pérez", "Desarrollador backend")).getId();
        servicio.crear(datos("0926687856", "Andrea Cedeño", "Analista de datos"));
        servicio.cambiarEstado(luis, Estado.ENTREVISTA);

        assertEquals(List.of("Andrea Cedeño", "Luis Pérez", "María Ordóñez"), nombres(servicio.listar(null, null, null)));
        assertEquals(List.of("Andrea Cedeño", "María Ordóñez"), nombres(servicio.listar("Analista de datos", null, "")));
        assertEquals(List.of("Luis Pérez"), nombres(servicio.listar(null, Estado.ENTREVISTA, null)));
        assertEquals(List.of("Luis Pérez"), nombres(servicio.listar(null, null, " 171003")));
    }

    @Test
    void buscarYEliminarAvisanSiNoExiste() {
        assertEquals("No existe la postulación 9.",
                assertThrows(NoEncontradoException.class, () -> servicio.buscar(9)).getMessage());
        long id = servicio.crear(datos("0102030400", "María Ordóñez", "Analista de datos")).getId();
        servicio.eliminar(id);
        assertThrows(NoEncontradoException.class, () -> servicio.eliminar(id));
    }

    private static List<String> nombres(List<Postulante> lista) {
        return lista.stream().map(Postulante::getNombres).toList();
    }
}
