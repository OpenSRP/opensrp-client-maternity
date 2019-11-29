package org.smartregister.maternity.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
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