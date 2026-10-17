package com.michicorp.postulantes.dominio;

/** Validación de la cédula ecuatoriana. */
public final class Cedula {

    private Cedula() {
    }

    /** Provincia 01-24 (o 30), tercer dígito menor que 6 y verificador módulo 10. */
    public static boolean esValida(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (!((provincia >= 1 && provincia <= 24) || provincia == 30) || cedula.charAt(2) - '0' >= 6) {
            return false;
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = (cedula.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += digito > 9 ? digito - 9 : digito;
        }
        return (10 - suma % 10) % 10 == cedula.charAt(9) - '0';
    }
}
