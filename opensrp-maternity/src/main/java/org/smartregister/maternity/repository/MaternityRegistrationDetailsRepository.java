package org.smartregister.maternity.repository;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.HashMap;

import timber.log.Timber;

public class MaternityRegistrationDetailsRepository extends BaseRepository {

    public HashMap<String, String> findByBaseEntityId(@NonNull String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "select * from " + MaternityDbConstants.Table.MATERNITY_REGISTRATION_DETAILS +
                                " where " + MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + " = '" + baseEntityId + "' limit 1").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }
}
