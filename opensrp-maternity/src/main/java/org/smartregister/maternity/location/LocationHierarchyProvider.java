package org.smartregister.maternity.location;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-28
 */

public interface LocationHierarchyProvider {

    @NonNull
    ArrayList<String> getLocationLevels();

    @NonNull
    ArrayList<String> getHealthFacilityLevels();

    @NonNull
    ArrayList<String> getAllowedLevels();

    @NonNull
    String getDefaultLevel();
}
