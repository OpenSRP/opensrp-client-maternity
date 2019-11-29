package org.smartregister.opd.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public interface FilePath {

    interface FOLDER {

        String CONFIG_FOLDER_PATH = "config/";
    }

    interface FILE {

        String OPD_PROFILE_OVERVIEW = "opd-profile-overview.yml";
        String OPD_VISIT_ROW = "opd-visit-row.yml";
    }
}