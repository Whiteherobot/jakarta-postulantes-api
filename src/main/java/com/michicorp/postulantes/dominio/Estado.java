package com.michicorp.postulantes.dominio;

import java.util.Set;

/** Etapas de una postulación. Contratado y descartado son finales. */
public enum Estado {
    POSTULADO("Postulado"),
    ENTREVISTA("En entrevista"),
    CONTRATADO("Contratado"),
    DESCARTADO("Descartado");

    private final String nombre;

    Estado(String nombre) {
        this.nombre = nombre;
    }

    public String nombre() {
        return nombre;
    }

    public Set<Estado> siguientes() {
        return switch (this) {
            case POSTULADO -> Set.of(ENTREVISTA, DESCARTADO);
            case ENTREVISTA -> Set.of(CONTRATADO, DESCARTADO);
            case CONTRATADO, DESCARTADO -> Set.of();
        };
    }

    public boolean puedePasarA(Estado otro) {
        return siguientes().contains(otro);
    }
}
