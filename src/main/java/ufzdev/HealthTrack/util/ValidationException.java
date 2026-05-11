package ufzdev.HealthTrack.util;

/**
 * Excepción usada para indicar errores de validación que deben mostrarse al usuario.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

