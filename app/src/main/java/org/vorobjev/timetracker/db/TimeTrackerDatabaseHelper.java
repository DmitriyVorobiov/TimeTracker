package org.vorobjev.timetracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.vorobjev.timetracker.entity.CategoryEntity;
import org.vorobjev.timetracker.entity.PhotoEntity;
import org.vorobjev.timetracker.entity.RecordEntity;

import java.sql.SQLException;

public class TimeTrackerDatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "tt_db";
    public static final int DATABASE_VERSION = 1;

    Class[] TABLES = new Class[]{
            CategoryEntity.class,
            PhotoEntity.class,
            RecordEntity.class
    };

    public TimeTrackerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        createTables(new AndroidConnectionSource(db));
    }

    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        ConnectionSource source = new AndroidConnectionSource(db);
        try {
            for (Class table : TABLES) {
                TableUtils.dropTable(source, table, true);
            }
        } catch (SQLException e) {
        }
        createTables(source);
    }

    private void createTables(ConnectionSource source) {
        try {
            for (Class table : TABLES) {
                TableUtils.createTable(source, table);
            }
        } catch (Exception e) {
        }
    }
}
