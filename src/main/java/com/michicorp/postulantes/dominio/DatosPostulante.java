package com.michicorp.postulantes.dominio;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Lo que llega del cliente al registrar o editar una postulación. */
public record DatosPostulante(String cedula, String nombres, String correo, String puesto, Integer experiencia) {

    public static final List<String> PUESTOS = List.of(
            "Desarrollador backend", "Desarrollador frontend", "Analista de datos", "Soporte técnico");

    /** Sin espacios sobrantes y con el correo en minúsculas. */
    public DatosPostulante normalizado() {
        return new DatosPostulante(
                recortar(cedula),
                recortar(nombres).replaceAll("\\s+", " "),
                recortar(correo).toLowerCase(Locale.ROOT),
                puesto,
                experiencia);
    }

    /** Errores por campo, con las mismas claves que usa el cliente. Vacío si es válido. */
    public Map<String, String> errores() {
        Map<String, String> errores = new LinkedHashMap<>();
        if (!Cedula.esValida(cedula)) {
            errores.put("cedula", "La cédula no es válida.");
        }
        if (nombres == null || !nombres.matches("[\\p{L} ]{3,80}")) {
            errores.put("nombres", "Escriba nombres y apellidos (solo letras).");
        }
        if (correo == null || !correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}")) {
            errores.put("correo", "El correo no es válido.");
        }
        if (puesto == null || !PUESTOS.contains(puesto)) {
            errores.put("puesto", "Elija un puesto.");
        }
        if (experiencia == null || experiencia < 0 || experiencia > 50) {
            errores.put("experiencia", "Años de experiencia entre 0 y 50.");
        }
        return errores;
    }

    private static String recortar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
