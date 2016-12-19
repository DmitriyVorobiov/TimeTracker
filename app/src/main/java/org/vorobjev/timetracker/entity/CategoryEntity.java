package org.vorobjev.timetracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.vorobjev.timetracker.dao.CategoryDao;

@DatabaseTable(tableName = "category", daoClass = CategoryDao.class)
public class CategoryEntity {

    @DatabaseField(columnName = "id", id = false, generatedId = true, allowGeneratedIdInsert = true)
    protected int id;
    @DatabaseField(columnName = "category_name")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
