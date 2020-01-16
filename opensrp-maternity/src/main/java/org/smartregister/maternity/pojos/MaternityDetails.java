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
    private boolean pendingDiagnoseAndTreat;
    private boolean pendingOutcome = true;
    private String conceptionDate;

    @Nullable
    private Date currentVisitStartDate;

    @Nullable
    private Date currentVisitEndDate;

    private String currentVisitId;

    private Date createdAt;

    public MaternityDetails() {
    }

    public MaternityDetails(String baseEntityId, String currentVisitId) {
        this.baseEntityId = baseEntityId;
        this.currentVisitId = currentVisitId;
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

    public boolean isPendingDiagnoseAndTreat() {
        return pendingDiagnoseAndTreat;
    }

    public void setPendingDiagnoseAndTreat(boolean pendingDiagnoseAndTreat) {
        this.pendingDiagnoseAndTreat = pendingDiagnoseAndTreat;
    }

    @Nullable
    public Date getCurrentVisitStartDate() {
        return currentVisitStartDate;
    }

    public void setCurrentVisitStartDate(@Nullable Date currentVisitStartDate) {
        this.currentVisitStartDate = currentVisitStartDate;
    }

    @Nullable
    public Date getCurrentVisitEndDate() {
        return currentVisitEndDate;
    }

    public void setCurrentVisitEndDate(@Nullable Date currentVisitEndDate) {
        this.currentVisitEndDate = currentVisitEndDate;
    }

    public String getCurrentVisitId() {
        return currentVisitId;
    }

    public void setCurrentVisitId(String currentVisitId) {
        this.currentVisitId = currentVisitId;
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
}
