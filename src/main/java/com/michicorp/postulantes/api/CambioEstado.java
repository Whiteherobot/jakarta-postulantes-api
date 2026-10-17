package com.michicorp.postulantes.api;

import com.michicorp.postulantes.dominio.Estado;

/** Cuerpo de PUT /postulantes/{id}/estado. */
public record CambioEstado(Estado estado) {
}
