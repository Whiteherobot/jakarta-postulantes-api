package com.michicorp.postulantes.api;

import com.michicorp.postulantes.servicio.ConflictoException;
import com.michicorp.postulantes.servicio.DatosInvalidosException;
import com.michicorp.postulantes.servicio.NoEncontradoException;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

/** Traduce las excepciones del servicio a respuestas JSON con el código que corresponde. */
public final class ErroresApi {

    private ErroresApi() {
    }

    static Response json(Response.Status estado, Object cuerpo) {
        return Response.status(estado).type(MediaType.APPLICATION_JSON).entity(cuerpo).build();
    }

    /** 400 {"errores": {"campo": "mensaje"}} */
    @Provider
    public static class DatosInvalidos implements ExceptionMapper<DatosInvalidosException> {
        @Override
        public Response toResponse(DatosInvalidosException e) {
            return json(Response.Status.BAD_REQUEST, Map.of("errores", e.getErrores()));
        }
    }

    /** 404 {"mensaje": "..."} */
    @Provider
    public static class NoEncontrado implements ExceptionMapper<NoEncontradoException> {
        @Override
        public Response toResponse(NoEncontradoException e) {
            return json(Response.Status.NOT_FOUND, Map.of("mensaje", e.getMessage()));
        }
    }

    /** 409 {"mensaje": "..."} */
    @Provider
    public static class Conflicto implements ExceptionMapper<ConflictoException> {
        @Override
        public Response toResponse(ConflictoException e) {
            return json(Response.Status.CONFLICT, Map.of("mensaje", e.getMessage()));
        }
    }

    /** 400 cuando el cuerpo no se puede leer (JSON mal formado o un estado que no existe). */
    @Provider
    public static class CuerpoIlegible implements ExceptionMapper<ProcessingException> {
        @Override
        public Response toResponse(ProcessingException e) {
            return json(Response.Status.BAD_REQUEST, Map.of("mensaje", "El cuerpo de la petición no es válido."));
        }
    }

    /** Igual que el anterior, por si JSON-B lanza su excepción sin envolver. */
    @Provider
    public static class JsonIlegible implements ExceptionMapper<JsonbException> {
        @Override
        public Response toResponse(JsonbException e) {
            return json(Response.Status.BAD_REQUEST, Map.of("mensaje", "El cuerpo de la petición no es válido."));
        }
    }
}
