package org.smartregister.maternity.utils;

import android.support.annotation.NonNull;

import org.smartregister.maternity.location.LocationHierarchyProvider;

import java.util.ArrayList;

public class DefaultMaternityLocationHierarchyProvider implements LocationHierarchyProvider {

    public static final String HEALTH_FACILITY = "Health Facility";
    public static final String FACILITY = "Facility";

    @NonNull
    public ArrayList<String> getLocationLevels() {
        ArrayList<String> allLevels = new ArrayList<>();
        allLevels.add("Country");
        allLevels.add("Province");
        allLevels.add("Department");
        allLevels.add(HEALTH_FACILITY);
        allLevels.add("Zone");
        allLevels.add("Residential Area");
        allLevels.add(FACILITY);
        return allLevels;
    }

    @NonNull
    public ArrayList<String> getHealthFacilityLevels() {
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");
        healthFacilities.add("Department");
        healthFacilities.add(HEALTH_FACILITY);
        healthFacilities.add(FACILITY);
        return healthFacilities;
    }

    @NonNull
    @Override
    public ArrayList<String> getAllowedLevels() {
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add(HEALTH_FACILITY);
        healthFacilities.add(FACILITY);
        return healthFacilities;
    }

    @NonNull
    @Override
    public String getDefaultLevel() {
        return HEALTH_FACILITY;
    }

}
