package ec.edu.ups.pruebaxx.pruebaxx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CandidatoDAO {

    @PersistenceContext
    private EntityManager em;

    public List<Candidato> findAll() {
        return em.createQuery("SELECT c FROM Candidato c", Candidato.class).getResultList();
    }

    public Candidato findById(Long id) {
        return em.find(Candidato.class, id);
    }

    @Transactional
    public void save(Candidato candidato) {
        if (candidato.getId() == null) {
            em.persist(candidato);
        } else {
            em.merge(candidato);
        }
    }

    public Candidato buscar(int id) {
        return em.find(Candidato.class, Long.valueOf(id));
    }

    @Transactional
    public void insertar(Candidato candidato) {
        em.persist(candidato);
    }
}
