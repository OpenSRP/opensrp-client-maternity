package org.smartregister.opd.sample.utils;

import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-18
 */

public class Utils {

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final String FACILITY = "Facility";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

}
