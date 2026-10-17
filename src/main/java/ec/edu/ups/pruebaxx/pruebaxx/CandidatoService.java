package ec.edu.ups.pruebaxx.pruebaxx;

import jakarta.inject.Inject;
import java.util.List;

public class CandidatoService {

    @Inject
    private CandidatoDAO candidatoDAO;

    // Obtener todos los candidatos
    public List<Candidato> listar() {
        return candidatoDAO.findAll();
    }

    // Buscar candidato por id
    public Candidato buscar(int id) {
        return candidatoDAO.buscar(id);
    }

    // Insertar un nuevo candidato
    public void insertar(Candidato candidato) {
        candidatoDAO.insertar(candidato);
    }

    // Guardar (insertar o actualizar) un candidato
    public void guardar(Candidato candidato) {
        candidatoDAO.save(candidato);
    }
}
