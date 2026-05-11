package ufzdev.HealthTrack.dao;

import ufzdev.HealthTrack.models.HealthMetricModel;
import java.util.List;

/**
 * Interfaz DAO para mediciones clinicas (HealthMetricModel).
 */
public interface HealthMetricDao {
    /**
     * Guarda una medición en la base de datos.
     */
    void save(HealthMetricModel metric) throws Exception;

    /**
     * Obtiene una medición por ID.
     */
    HealthMetricModel getById(String id) throws Exception;

    /**
     * Obtiene todas las mediciones de un usuario ordenadas por fecha (desc).
     */
    List<HealthMetricModel> getByUserId(String userId) throws Exception;

    /**
     * Obtiene mediciones de un usuario filtradas por tipo de métrica.
     */
    List<HealthMetricModel> getByUserIdAndType(String userId, String metricType) throws Exception;

    /**
     * Obtiene los últimos N registros de un usuario.
     */
    List<HealthMetricModel> getLatestByUserId(String userId, int limit) throws Exception;

    /**
     * Elimina una medición por ID.
     */
    void delete(String id) throws Exception;
}

