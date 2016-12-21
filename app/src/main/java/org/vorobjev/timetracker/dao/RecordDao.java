package org.vorobjev.timetracker.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import org.vorobjev.timetracker.entity.RecordEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class RecordDao extends BaseDaoImpl<RecordEntity, Integer> {

    public RecordDao(ConnectionSource connectionSource, Class<RecordEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<RecordEntity> getRecords() throws SQLException {
        return queryBuilder().query();
    }

    public void add(final RecordEntity category) throws SQLException {
        callBatchTasks(new Callable<RecordEntity>() {
            public RecordEntity call() throws Exception {
                createOrUpdate(category);
                return null;
            }
        });
    }

    public List<RecordEntity> findRecord(int id) throws SQLException {
        return queryBuilder().where().eq("id", new SelectArg(id)).query();
    }

}
