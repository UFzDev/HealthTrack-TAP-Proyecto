package ufzdev.HealthTrack.dao;

import ufzdev.HealthTrack.models.SystemLogModel;
import java.util.List;

public interface SystemLogDao {
    void save(SystemLogModel log) throws Exception;
    List<SystemLogModel> listLatest(int limit) throws Exception;
    List<SystemLogModel> listAll() throws Exception;
}
