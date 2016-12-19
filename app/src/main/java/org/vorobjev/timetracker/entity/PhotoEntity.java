package org.vorobjev.timetracker.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.vorobjev.timetracker.dao.PhotoDao;

@DatabaseTable(tableName = "photo", daoClass = PhotoDao.class)
public class PhotoEntity {
    @DatabaseField(columnName = "id", id = false, generatedId = true, allowGeneratedIdInsert = true)
    protected int id;
    @DatabaseField(columnName = "photo_bytes", dataType = DataType.BYTE_ARRAY)
    byte[] imageBytes;

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
