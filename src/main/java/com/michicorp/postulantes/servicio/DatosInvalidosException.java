package com.michicorp.postulantes.servicio;

import java.util.Map;

/** Datos que no cumplen las reglas; la API responde 400 con los errores por campo. */
public class DatosInvalidosException extends RuntimeException {

    private final Map<String, String> errores;

    public DatosInvalidosException(Map<String, String> errores) {
        super("Datos inválidos: " + errores.keySet());
        this.errores = Map.copyOf(errores);
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
