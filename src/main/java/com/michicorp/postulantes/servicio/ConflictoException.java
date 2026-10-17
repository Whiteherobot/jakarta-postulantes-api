package com.michicorp.postulantes.servicio;

/** La operación choca con el estado actual (por ejemplo, una transición no permitida); la API responde 409. */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
