package org.smartregister.opd.pojos;

import android.support.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public class OpdCheckIn {

    private int id;
    private String formSubmissionId;
    private String visitId;
    private String baseEntityId;
    private String pregnancyStatus;
    private String hasHivTestPreviously;
    private String hivResultsPreviously;
    private String isTakingArt;
    private String currentHivResult;
    private String visitType;
    private String appointmentScheduledPreviously;
    private String appointmentDueDate;
    private long updatedAt;
    private long createdAt;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getPregnancyStatus() {
        return pregnancyStatus;
    }

    public void setPregnancyStatus(String pregnancyStatus) {
        this.pregnancyStatus = pregnancyStatus;
    }

    public String getHasHivTestPreviously() {
        return hasHivTestPreviously;
    }

    public void setHasHivTestPreviously(String hasHivTestPreviously) {
        this.hasHivTestPreviously = hasHivTestPreviously;
    }

    public String getHivResultsPreviously() {
        return hivResultsPreviously;
    }

    public void setHivResultsPreviously(String hivResultsPreviously) {
        this.hivResultsPreviously = hivResultsPreviously;
    }

    public String getIsTakingArt() {
        return isTakingArt;
    }

    public void setIsTakingArt(String isTakingArt) {
        this.isTakingArt = isTakingArt;
    }

    public String getCurrentHivResult() {
        return currentHivResult;
    }

    public void setCurrentHivResult(String currentHivResult) {
        this.currentHivResult = currentHivResult;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getAppointmentScheduledPreviously() {
        return appointmentScheduledPreviously;
    }

    public void setAppointmentScheduledPreviously(String appointmentScheduledPreviously) {
        this.appointmentScheduledPreviously = appointmentScheduledPreviously;
    }

    @Nullable
    public String getAppointmentDueDate() {
        return appointmentDueDate;
    }

    public void setAppointmentDueDate(String appointmentDueDate) {
        this.appointmentDueDate = appointmentDueDate;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
