package org.smartregister.opd.pojos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.opd.utils.DefaultOpdLocationUtils;

import java.util.ArrayList;

public class OpdMetadata {

    private String opdRegistrationFormName;

    private String tableName;

    private String registerEventType;

    private String updateEventType;

    private String config;

    private Class opdFormActivity;

    private Class profileActivity;

    private boolean formWizardValidateRequiredFieldsBefore;

    private ArrayList<String> locationLevels;

    private ArrayList<String> healthFacilityLevels;

    public OpdMetadata(@NonNull String opdRegistrationFormName, @NonNull String tableName, @NonNull String registerEventType, @NonNull String updateEventType,
                       @NonNull String config, @NonNull Class opdFormActivity, @Nullable Class profileActivity, boolean formWizardValidateRequiredFieldsBefore) {
        this.opdRegistrationFormName = opdRegistrationFormName;
        this.tableName = tableName;
        this.registerEventType = registerEventType;
        this.updateEventType = updateEventType;
        this.config = config;
        this.opdFormActivity = opdFormActivity;
        this.profileActivity = profileActivity;
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    public String getOpdRegistrationFormName() {
        return opdRegistrationFormName;
    }

    public void setOpdRegistrationFormName(String opdRegistrationFormName) {
        this.opdRegistrationFormName = opdRegistrationFormName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRegisterEventType() {
        return registerEventType;
    }

    public void setRegisterEventType(String registerEventType) {
        this.registerEventType = registerEventType;
    }

    public String getUpdateEventType() {
        return updateEventType;
    }

    public void setUpdateEventType(String updateEventType) {
        this.updateEventType = updateEventType;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public Class getOpdFormActivity() {
        return opdFormActivity;
    }

    public void setOpdFormActivity(Class opdFormActivity) {
        this.opdFormActivity = opdFormActivity;
    }

    public Class getProfileActivity() {
        return profileActivity;
    }

    public void setProfileActivity(Class profileActivity) {
        this.profileActivity = profileActivity;
    }

    public boolean isFormWizardValidateRequiredFieldsBefore() {
        return formWizardValidateRequiredFieldsBefore;
    }

    public void setFormWizardValidateRequiredFieldsBefore(boolean formWizardValidateRequiredFieldsBefore) {
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    @NonNull
    public ArrayList<String> getLocationLevels() {
        if (locationLevels == null) {
            locationLevels = DefaultOpdLocationUtils.getLocationLevels();
        }

        return locationLevels;
    }

    public void setLocationLevels(ArrayList<String> locationLevels) {
        this.locationLevels = locationLevels;
    }

    @NonNull
    public ArrayList<String> getHealthFacilityLevels() {
        if (healthFacilityLevels == null) {
            healthFacilityLevels = DefaultOpdLocationUtils.getHealthFacilityLevels();
        }

        return healthFacilityLevels;
    }

    public void setHealthFacilityLevels(ArrayList<String> healthFacilityLevels) {
        this.healthFacilityLevels = healthFacilityLevels;
    }
}
