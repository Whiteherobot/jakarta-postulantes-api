package com.michicorp.postulantes.datos;

import com.michicorp.postulantes.dominio.Postulante;
import com.michicorp.postulantes.servicio.PostulanteRepositorio;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JpaPostulanteRepositorio implements PostulanteRepositorio {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Postulante> listar() {
        return em.createQuery("SELECT p FROM Postulante p", Postulante.class).getResultList();
    }

    @Override
    public Optional<Postulante> buscar(long id) {
        return Optional.ofNullable(em.find(Postulante.class, id));
    }

    @Override
    public boolean existe(String cedula, String puesto, Long excluir) {
        return em.createQuery("""
                SELECT COUNT(p) FROM Postulante p
                WHERE p.cedula = :cedula AND p.puesto = :puesto AND (:excluir IS NULL OR p.id <> :excluir)
                """, Long.class)
                .setParameter("cedula", cedula)
                .setParameter("puesto", puesto)
                .setParameter("excluir", excluir)
                .getSingleResult() > 0;
    }

    @Override
    public Postulante guardar(Postulante postulante) {
        if (postulante.getId() == null) {
            em.persist(postulante);
            return postulante;
        }
        return em.merge(postulante);
    }

    @Override
    public void eliminar(Postulante postulante) {
        em.remove(em.contains(postulante) ? postulante : em.merge(postulante));
    }
}
