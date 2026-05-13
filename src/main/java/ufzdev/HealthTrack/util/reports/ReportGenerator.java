package ufzdev.HealthTrack.util.reports;

import java.nio.file.Path;
import java.util.List;

public interface ReportGenerator {
    /**
     * Genera un reporte basado en datos tabulares genéricos.
     * @param title Título del reporte.
     * @param headers Nombres de las columnas.
     * @param rows Datos de las filas (cada String[] representa una fila).
     * @param fileName Nombre base del archivo (sin extensión).
     * @return Path del archivo generado.
     * @throws Exception Si ocurre un error en la generación.
     */
    Path generate(String title, String[] headers, List<String[]> rows, String fileName) throws Exception;
}
