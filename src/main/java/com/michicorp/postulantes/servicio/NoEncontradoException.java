package com.michicorp.postulantes.servicio;

/** La postulación pedida no existe; la API responde 404. */
public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(long id) {
        super("No existe la postulación " + id + ".");
    }
}
