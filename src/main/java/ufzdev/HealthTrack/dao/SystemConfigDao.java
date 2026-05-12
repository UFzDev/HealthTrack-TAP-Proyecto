package ufzdev.HealthTrack.dao;

import ufzdev.HealthTrack.models.SystemConfigModel;

public interface SystemConfigDao {
    SystemConfigModel get() throws Exception;
    void save(SystemConfigModel config) throws Exception;
}
