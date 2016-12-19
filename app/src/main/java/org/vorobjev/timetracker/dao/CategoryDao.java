package org.vorobjev.timetracker.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import org.vorobjev.timetracker.entity.CategoryEntity;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class CategoryDao extends BaseDaoImpl<CategoryEntity, Integer> {

    public CategoryDao(ConnectionSource connectionSource, Class<CategoryEntity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<CategoryEntity> getCategories() throws SQLException {
        return queryBuilder().query();
    }

    public List<CategoryEntity> findCategory(String name) throws SQLException {
        return queryBuilder().where().eq("category_name", new SelectArg(name)).query();
    }

    public void add(final CategoryEntity category) throws SQLException {
        callBatchTasks(new Callable<CategoryEntity>() {
            public CategoryEntity call() throws Exception {
                createOrUpdate(category);
                return null;
            }
        });
    }

    public void clearEntities() throws SQLException {
        DeleteBuilder<CategoryEntity, Integer> deleteBuilder = deleteBuilder();
        deleteBuilder.delete();
    }

    public void update(final Collection<CategoryEntity> cats) throws SQLException {
        callBatchTasks(new Callable<CategoryEntity>() {
            public CategoryEntity call() throws Exception {
                for (CategoryEntity cat : cats) {
                    createOrUpdate(cat);
                }
                return null;
            }
        });
    }
}
