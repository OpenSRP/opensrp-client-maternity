package org.smartregister.maternity.pojos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityBaseDetails {

    private int id;
    private String baseEntityId;
    private Date eventDate;
    private Date createdAt;
    private HashMap<String, String> properties = new HashMap<>();


    public MaternityBaseDetails() {
    }

    public MaternityBaseDetails(@NonNull String baseEntityId, @NonNull Date eventDate, @NonNull HashMap<String, String> properties) {
        this.baseEntityId = baseEntityId;
        this.eventDate = eventDate;
        this.properties = properties;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public String get(@NonNull String property) {
        return properties.get(property);
    }

    public String put(@NonNull String property, @Nullable String value) {
        return properties.put(property, value);
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
