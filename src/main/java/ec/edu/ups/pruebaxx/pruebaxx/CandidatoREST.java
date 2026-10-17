package ec.edu.ups.pruebaxx.pruebaxx;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/candidatos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CandidatoREST {

    @Inject
    private CandidatoDAO candidatoDAO;

    @GET
    public List<Candidato> getAll() {
        return candidatoDAO.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Candidato c = candidatoDAO.findById(id);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(c).build();
    }

    @POST
    public Response create(Candidato candidato) {
        candidatoDAO.save(candidato);
        return Response.status(Response.Status.CREATED)
                       .entity(candidato) // 👈 Devuelve el candidato como JSON
                       .build();
    }

}
