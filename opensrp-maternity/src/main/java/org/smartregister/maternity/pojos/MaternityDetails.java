package org.smartregister.maternity.pojos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityDetails {

    private int id;
    private String baseEntityId;
    private String gravida;
    private String para;
    private String hivStatus;
    private boolean pendingOutcome = true;
    private String conceptionDate;
    private Date createdAt;

    public MaternityDetails() {
    }

    public MaternityDetails(@NonNull String baseEntityId, @NonNull String gravida, @NonNull String conceptionDate) {
        this.baseEntityId = baseEntityId;
        this.gravida = gravida;
        this.conceptionDate = conceptionDate;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPendingOutcome() {
        return pendingOutcome;
    }

    public void setPendingOutcome(boolean pendingOutcome) {
        this.pendingOutcome = pendingOutcome;
    }

    public String getConceptionDate() {
        return conceptionDate;
    }

    public void setConceptionDate(@NonNull String conceptionDate) {
        this.conceptionDate = conceptionDate;
    }

    public String getGravida() {
        return gravida;
    }

    public void setGravida(@NonNull String gravida) {
        this.gravida = gravida;
    }

    public String getPara() {
        return para;
    }

    public void setPara(@Nullable String para) {
        this.para = para;
    }

    public String getHivStatus() {
        return hivStatus;
    }

    public void setHivStatus(@Nullable String hivStatus) {
        this.hivStatus = hivStatus;
    }
}
