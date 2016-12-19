package org.vorobjev.timetracker.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class AbstractDao<T> extends BaseDaoImpl<T, Integer> {

    protected AbstractDao(ConnectionSource connectionSource, Class<T> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

}
