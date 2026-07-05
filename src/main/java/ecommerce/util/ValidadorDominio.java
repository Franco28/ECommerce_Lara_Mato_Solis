package ecommerce.util;

import ecommerce.exception.DatosInvalidosException;

/**
 * Validador común para evitar repetir reglas básicas en todas las entidades.
 */
public final class ValidadorDominio {

    private ValidadorDominio() {
    }

    public static void validarTextoObligatorio(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new DatosInvalidosException(mensaje);
        }
    }

    public static <T> T validarObjetoObligatorio(T valor, String mensaje) {
        if (valor == null) {
            throw new DatosInvalidosException(mensaje);
        }
        return valor;
    }

    public static void validarIdNoNegativo(int id, String mensaje) {
        if (id < 0) {
            throw new DatosInvalidosException(mensaje);
        }
    }

    public static void validarEnteroMayorACero(int valor, String mensaje) {
        if (valor <= 0) {
            throw new DatosInvalidosException(mensaje);
        }
    }

    public static void validarEnteroNoNegativo(int valor, String mensaje) {
        if (valor < 0) {
            throw new DatosInvalidosException(mensaje);
        }
    }

    public static void validarDecimalMayorACero(double valor, String mensaje) {
        if (valor <= 0) {
            throw new DatosInvalidosException(mensaje);
        }
    }

    public static void validarDecimalNoNegativo(double valor, String mensaje) {
        if (valor < 0) {
            throw new DatosInvalidosException(mensaje);
        }
    }

    public static void validarEmail(String email) {
        validarTextoObligatorio(email, "El email es obligatorio.");
        String normalizado = email.trim();
        if (!normalizado.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new DatosInvalidosException("El email no tiene un formato válido.");
        }
    }
}
