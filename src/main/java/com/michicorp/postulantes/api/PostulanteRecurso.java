package com.michicorp.postulantes.api;

import com.michicorp.postulantes.dominio.DatosPostulante;
import com.michicorp.postulantes.dominio.Estado;
import com.michicorp.postulantes.dominio.Postulante;
import com.michicorp.postulantes.servicio.PostulanteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Context;
import java.util.List;

@Path("/postulantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PostulanteRecurso {

    @Inject
    PostulanteService servicio;

    @GET
    public List<Postulante> listar(@QueryParam("puesto") String puesto, @QueryParam("estado") Estado estado,
            @QueryParam("q") String texto) {
        return servicio.listar(puesto, estado, texto);
    }

    @GET
    @Path("/{id}")
    public Postulante buscar(@PathParam("id") long id) {
        return servicio.buscar(id);
    }

    @POST
    public Response crear(DatosPostulante datos, @Context UriInfo uri) {
        Postulante creado = servicio.crear(datos);
        return Response.created(uri.getAbsolutePathBuilder().path(String.valueOf(creado.getId())).build())
                .entity(creado)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Postulante actualizar(@PathParam("id") long id, DatosPostulante datos) {
        return servicio.actualizar(id, datos);
    }

    @PUT
    @Path("/{id}/estado")
    public Postulante cambiarEstado(@PathParam("id") long id, CambioEstado cambio) {
        return servicio.cambiarEstado(id, cambio == null ? null : cambio.estado());
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") long id) {
        servicio.eliminar(id);
        return Response.noContent().build();
    }
}
