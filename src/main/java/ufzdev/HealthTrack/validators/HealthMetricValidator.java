package ufzdev.HealthTrack.validators;

import ufzdev.HealthTrack.util.ValidationException;

import javafx.scene.control.TextField;
import java.time.LocalDateTime;

/**
 * Validaciones para mediciones rápidas (quick record).
 */
public class HealthMetricValidator {

    public static void validateQuickRecord(TextField bpSystolicInput,
                                           TextField bpDiastolicInput,
                                           TextField glucoseInput,
                                           TextField heartRateInput,
                                           TextField weightInput,
                                           TextField heightInput) throws ValidationException {

        // Validar que si hay texto, sea numérico
        if (isInvalidNumber(bpSystolicInput, false)
                || isInvalidNumber(bpDiastolicInput, false)
                || isInvalidNumber(glucoseInput, false)
                || isInvalidNumber(heartRateInput, false)
                || isInvalidNumber(weightInput, false)
                || isInvalidNumber(heightInput, false)) {
            throw new ValidationException("Revisa los campos. Todos deben contener números válidos si están completos.");
        }

        // Parsear en valores si están presentes
        double bpSyst = bpSystolicInput.getText() == null || bpSystolicInput.getText().trim().isEmpty() ? Double.NaN : Double.parseDouble(bpSystolicInput.getText().trim());
        double bpDiast = bpDiastolicInput.getText() == null || bpDiastolicInput.getText().trim().isEmpty() ? Double.NaN : Double.parseDouble(bpDiastolicInput.getText().trim());
        double glucose = glucoseInput.getText() == null || glucoseInput.getText().trim().isEmpty() ? Double.NaN : Double.parseDouble(glucoseInput.getText().trim());
        double hr = heartRateInput.getText() == null || heartRateInput.getText().trim().isEmpty() ? Double.NaN : Double.parseDouble(heartRateInput.getText().trim());
        double weight = weightInput.getText() == null || weightInput.getText().trim().isEmpty() ? Double.NaN : Double.parseDouble(weightInput.getText().trim());
        double height = heightInput.getText() == null || heightInput.getText().trim().isEmpty() ? Double.NaN : Double.parseDouble(heightInput.getText().trim());

        // Rangos razonables
        if (!Double.isNaN(bpSyst) && (bpSyst < 50 || bpSyst > 250)) {
            throw new ValidationException("Presión sistólica fuera de rango plausible (50-250 mmHg).");
        }

        if (!Double.isNaN(bpDiast) && (bpDiast < 30 || bpDiast > 150)) {
            throw new ValidationException("Presión diastólica fuera de rango plausible (30-150 mmHg).");
        }

        if (!Double.isNaN(bpSyst) && !Double.isNaN(bpDiast) && bpSyst <= bpDiast) {
            throw new ValidationException("La presión sistólica debe ser mayor que la diastólica.");
        }

        if (!Double.isNaN(glucose) && (glucose < 20 || glucose > 800)) {
            throw new ValidationException("Glucosa fuera de rango plausible (20-800 mg/dL).");
        }

        if (!Double.isNaN(hr) && (hr < 20 || hr > 240)) {
            throw new ValidationException("Frecuencia cardíaca fuera de rango plausible (20-240 bpm).");
        }

        if (!Double.isNaN(weight) && (weight < 2 || weight > 500)) {
            throw new ValidationException("Peso fuera de rango plausible (2-500 kg).");
        }

        if (!Double.isNaN(height) && (height <= 0.2 || height > 3.0)) {
            throw new ValidationException("Altura fuera de rango plausible (0.2-3.0 m).");
        }

        // IMC coherencia si ambos presentes
        if (!Double.isNaN(weight) && !Double.isNaN(height)) {
            double imc = weight / (height * height);
            if (imc < 5 || imc > 80) {
                throw new ValidationException("IMC fuera de rango plausible (5-80). Revisa peso/altura.");
            }
        }

        // Fecha de registro: no puede ser futura (se valida en controlador si aplica)
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(now.plusMinutes(1))) { // dummy check to keep structure; real check in controller if passing recordedAt
            // no-op
        }
    }

    private static boolean isInvalidNumber(TextField field, boolean required) {
        if (field == null || field.getText() == null || field.getText().trim().isEmpty()) {
            return required;
        }

        try {
            Double.parseDouble(field.getText().trim());
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }
}

