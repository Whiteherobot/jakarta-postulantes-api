package com.michicorp.postulantes.servicio;

import com.michicorp.postulantes.dominio.Postulante;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Repositorio para las pruebas: guarda en un mapa y numera los ids como la base. */
class RepositorioEnMemoria implements PostulanteRepositorio {

    private final Map<Long, Postulante> datos = new LinkedHashMap<>();
    private long siguiente = 1;

    @Override
    public List<Postulante> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public Optional<Postulante> buscar(long id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public boolean existe(String cedula, String puesto, Long excluir) {
        return datos.values().stream().anyMatch(p -> p.getCedula().equals(cedula)
                && p.getPuesto().equals(puesto) && !Objects.equals(p.getId(), excluir));
    }

    @Override
    public Postulante guardar(Postulante postulante) {
        if (postulante.getId() == null) {
            postulante.asignarId(siguiente++);
        }
        datos.put(postulante.getId(), postulante);
        return postulante;
    }

    @Override
    public void eliminar(Postulante postulante) {
        datos.remove(postulante.getId());
    }
}
