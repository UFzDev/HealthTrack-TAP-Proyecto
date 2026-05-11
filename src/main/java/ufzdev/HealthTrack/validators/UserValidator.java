package ufzdev.HealthTrack.validators;

import ufzdev.HealthTrack.models.UserModel;
import ufzdev.HealthTrack.util.ValidationException;

import java.util.regex.Pattern;

/**
 * Validaciones básicas para registro y edición de usuarios.
 */
public class UserValidator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{3,30}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static void validateForRegistration(UserModel user) throws ValidationException {
        if (user == null) throw new ValidationException("Datos de usuario vacíos.");

        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("El nombre es obligatorio.");
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ValidationException("El nombre de usuario es obligatorio.");
        }

        if (!USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            throw new ValidationException("Nombre de usuario inválido. Solo se permiten letras, números, '.', '_' y '-' entre 3 y 30 caracteres.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("El correo electrónico es obligatorio.");
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new ValidationException("Correo electrónico inválido.");
        }

        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres.");
        }
    }
}

