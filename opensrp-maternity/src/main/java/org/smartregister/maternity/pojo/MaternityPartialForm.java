package org.smartregister.maternity.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MaternityPartialForm {
    private int id;
    private String baseEntityId;
    private String form;
    private String formType;
    private String createdAt;

    public MaternityPartialForm(String baseEntityId, @Nullable String formType) {
        this.baseEntityId = baseEntityId;
        this.formType = formType;
    }

    public MaternityPartialForm(int id, @NonNull String baseEntityId, @NonNull String form, @NonNull String formType, @NonNull String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.form = form;
        this.formType = formType;
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

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
