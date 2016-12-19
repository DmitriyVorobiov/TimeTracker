package org.vorobjev.timetracker.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import org.vorobjev.timetracker.entity.PhotoEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class PhotoDao extends BaseDaoImpl<PhotoEntity, Integer> {

    public PhotoDao(ConnectionSource connectionSource, Class<PhotoEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<PhotoEntity> getPhotos() throws SQLException {
        return queryBuilder().query();
    }

    public void add(final PhotoEntity category) throws SQLException {
        callBatchTasks(new Callable<PhotoEntity>() {
            public PhotoEntity call() throws Exception {
                createOrUpdate(category);
                return null;
            }
        });
    }

}