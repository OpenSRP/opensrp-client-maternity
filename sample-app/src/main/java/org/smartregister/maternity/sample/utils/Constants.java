package org.smartregister.maternity.sample.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-28
 */

public interface Constants {

    interface Table {
        String MATERNITY = "ec_client";
    }

    interface Columns {

        interface Maternity {

            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String MIDDLE_NAME = "middle_name";
            String DOB = "dob";
            String DOD = "dod";
            String LAST_INTERACTED_WITH = "last_interacted_with";
        }
    }
}
