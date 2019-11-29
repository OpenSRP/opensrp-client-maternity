package org.smartregister.opd.pojos;

import android.support.annotation.NonNull;

public class OpdDiagnosisAndTreatmentForm {
    private int id;
    private String baseEntityId;
    private String form;
    private String createdAt;

    public OpdDiagnosisAndTreatmentForm() {
    }

    public OpdDiagnosisAndTreatmentForm(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public OpdDiagnosisAndTreatmentForm(int id, @NonNull String baseEntityId, @NonNull String form, @NonNull String createdAt) {
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
