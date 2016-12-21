package org.vorobjev.timetracker;

import android.app.Application;

import org.vorobjev.timetracker.dao.CategoryDao;
import org.vorobjev.timetracker.db.TimeTrackerDatabaseHelper;
import org.vorobjev.timetracker.entity.CategoryEntity;

import java.sql.SQLException;
import java.util.ArrayList;

public class TimeTrackerApplication extends Application {

    static TimeTrackerApplication instance;
    TimeTrackerDatabaseHelper dbHelper;

    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new TimeTrackerDatabaseHelper(instance);
        fillCategories();
    }

    public static TimeTrackerApplication getInstance() {
        return instance;
    }

    public TimeTrackerDatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(TimeTrackerDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    private void fillCategories() {
        CategoryEntity cat1 = new CategoryEntity();
        cat1.setName("Work");
        CategoryEntity cat2 = new CategoryEntity();
        cat2.setName("Lunch");
        CategoryEntity cat3 = new CategoryEntity();
        cat3.setName("Relax");
        CategoryEntity cat4 = new CategoryEntity();
        cat4.setName("Cleaning");
        CategoryEntity cat5 = new CategoryEntity();
        cat5.setName("Sleep");
        try {
            ArrayList<CategoryEntity> cats = new ArrayList<>();
            cats.add(cat1);
            cats.add(cat2);
            cats.add(cat3);
            cats.add(cat4);
            cats.add(cat5);
            if (dbHelper.<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).getCategories().isEmpty()) {
                dbHelper.<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).update(cats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
