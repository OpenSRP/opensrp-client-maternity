package org.smartregister.maternity.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public interface FilePath {

    interface FOLDER {

        String CONFIG_FOLDER_PATH = "config/";
    }

    interface FILE {

        String MATERNITY_PROFILE_OVERVIEW = "maternity-profile-overview.yml";
        String MATERNITY_VISIT_ROW = "maternity-visit-row.yml";
    }
}