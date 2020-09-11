package org.smartregister.maternity.utils;

import androidx.annotation.NonNull;

import org.smartregister.maternity.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;

public class DefaultMaternityLocationUtils {

    @NonNull
    public static ArrayList<String> getLocationLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
    }

    @NonNull
    public static ArrayList<String> getHealthFacilityLevels() {
        return new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

}
