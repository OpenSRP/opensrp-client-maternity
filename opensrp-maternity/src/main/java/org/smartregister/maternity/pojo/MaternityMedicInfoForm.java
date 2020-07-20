package org.smartregister.maternity.pojo;

import android.support.annotation.NonNull;

public class MaternityMedicInfoForm {
    private int id;
    private String baseEntityId;
    private String form;
    private String createdAt;

    public MaternityMedicInfoForm() {
    }

    public MaternityMedicInfoForm(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public MaternityMedicInfoForm(int id, @NonNull String baseEntityId, @NonNull String form, @NonNull String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.form = form;
        this.createdAt = createdAt;
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

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
