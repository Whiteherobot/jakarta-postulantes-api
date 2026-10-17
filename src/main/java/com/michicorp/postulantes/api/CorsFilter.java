package com.michicorp.postulantes.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

/**
 * Permite que el cliente en Angular (otro origen, por ejemplo localhost:4200)
 * llame a la API. La API no usa cookies, así que alcanza con el origen "*".
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext peticion, ContainerResponseContext respuesta) {
        var cabeceras = respuesta.getHeaders();
        cabeceras.putSingle("Access-Control-Allow-Origin", "*");
        cabeceras.putSingle("Access-Control-Allow-Headers", "origin, content-type, accept");
        cabeceras.putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }
}
