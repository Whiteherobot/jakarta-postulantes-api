package com.michicorp.postulantes.servicio;

import com.michicorp.postulantes.dominio.Postulante;
import java.util.List;
import java.util.Optional;

/** Dónde se guardan las postulaciones (JPA en el servidor, memoria en las pruebas). */
public interface PostulanteRepositorio {

    List<Postulante> listar();

    Optional<Postulante> buscar(long id);

    /** ¿Hay otra postulación (distinta de `excluir`) de esa cédula a ese puesto? */
    boolean existe(String cedula, String puesto, Long excluir);

    Postulante guardar(Postulante postulante);

    void eliminar(Postulante postulante);
}
