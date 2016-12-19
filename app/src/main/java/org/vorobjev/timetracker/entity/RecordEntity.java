package org.vorobjev.timetracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.vorobjev.timetracker.dao.RecordDao;

@DatabaseTable(tableName = "record", daoClass = RecordDao.class)
public class RecordEntity {
    @DatabaseField(columnName = "id", id = false, generatedId = true, allowGeneratedIdInsert = true)
    protected int id;
    @DatabaseField(columnName = "time_start")
    Long timeStart;
    @DatabaseField(columnName = "time_end")
    Long timeEnd;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    CategoryEntity categoryEntity;
    @DatabaseField(columnName = "duration")
    Long duration;
    @DatabaseField(columnName = "description")
    String description;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    PhotoEntity photoEntity;

    public PhotoEntity getPhotoEntity() {
        return photoEntity;
    }

    public void setPhotoEntity(PhotoEntity photoEntity) {
        this.photoEntity = photoEntity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }

    public Long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

}
