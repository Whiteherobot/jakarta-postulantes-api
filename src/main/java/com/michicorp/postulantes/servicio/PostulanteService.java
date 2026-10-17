package com.michicorp.postulantes.servicio;

import com.michicorp.postulantes.dominio.DatosPostulante;
import com.michicorp.postulantes.dominio.Estado;
import com.michicorp.postulantes.dominio.Postulante;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Casos de uso de las postulaciones; cada uno corre en su propia transacción. */
@ApplicationScoped
@Transactional
public class PostulanteService {

    private static final Comparator<Postulante> MAS_RECIENTES_PRIMERO = Comparator
            .comparing(Postulante::getFechaPostulacion).reversed()
            .thenComparing(Postulante::getId, Comparator.reverseOrder());

    private PostulanteRepositorio repositorio;
    private Clock reloj;

    protected PostulanteService() {
        // Para el proxy de CDI.
    }

    @Inject
    public PostulanteService(PostulanteRepositorio repositorio) {
        this(repositorio, Clock.systemDefaultZone());
    }

    public PostulanteService(PostulanteRepositorio repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    /** Filtra por puesto, estado y texto (nombre o cédula); cualquiera puede ser null. */
    public List<Postulante> listar(String puesto, Estado estado, String texto) {
        String buscado = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        return repositorio.listar().stream()
                .filter(p -> puesto == null || puesto.isBlank() || p.getPuesto().equals(puesto))
                .filter(p -> estado == null || p.getEstado() == estado)
                .filter(p -> (p.getNombres() + " " + p.getCedula()).toLowerCase(Locale.ROOT).contains(buscado))
                .sorted(MAS_RECIENTES_PRIMERO)
                .toList();
    }

    public Postulante buscar(long id) {
        return repositorio.buscar(id).orElseThrow(() -> new NoEncontradoException(id));
    }

    public Postulante crear(DatosPostulante datos) {
        DatosPostulante limpios = validar(datos, null);
        return repositorio.guardar(Postulante.nueva(limpios, LocalDate.now(reloj)));
    }

    public Postulante actualizar(long id, DatosPostulante datos) {
        Postulante postulante = buscar(id);
        postulante.actualizar(validar(datos, id));
        return repositorio.guardar(postulante);
    }

    public Postulante cambiarEstado(long id, Estado estado) {
        if (estado == null) {
            throw new DatosInvalidosException(Map.of("estado", "Indique el estado."));
        }
        Postulante postulante = buscar(id);
        try {
            postulante.pasarA(estado);
        } catch (IllegalStateException e) {
            throw new ConflictoException(e.getMessage());
        }
        return repositorio.guardar(postulante);
    }

    public void eliminar(long id) {
        repositorio.eliminar(buscar(id));
    }

    private DatosPostulante validar(DatosPostulante datos, Long id) {
        DatosPostulante limpios = datos.normalizado();
        Map<String, String> errores = new java.util.LinkedHashMap<>(limpios.errores());
        if (!errores.containsKey("cedula") && !errores.containsKey("puesto")
                && repositorio.existe(limpios.cedula(), limpios.puesto(), id)) {
            errores.put("cedula", "Esta persona ya se postuló a ese puesto.");
        }
        if (!errores.isEmpty()) {
            throw new DatosInvalidosException(errores);
        }
        return limpios;
    }
}
