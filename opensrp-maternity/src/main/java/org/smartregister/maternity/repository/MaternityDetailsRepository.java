package org.smartregister.maternity.repository;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.BaseRepository;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityDetailsRepository extends BaseRepository {

    public Map<String, String> findByBaseEntityId(@NonNull String baseEntityId) {
        try {
            return MaternityLibrary.getInstance().context().getEventClientRepository()
                    .rawQuery(MaternityLibrary.getInstance().context().getEventClientRepository().getReadableDatabase(),
                            "select * from " + MaternityUtils.metadata().getTableName() +
                                    " where " + MaternityDbConstants.Column.Client.BASE_ENTITY_ID + " = '" + baseEntityId + "' limit 1").get(0);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }

    public HashMap<String, String> findMedicInfoByBaseEntityId(@NonNull String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                return rawQuery(getReadableDatabase(),
                        "select * from maternity_medic_info " +
                                " where " + MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + " = '" + baseEntityId + "' limit 1").get(0);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Timber.e(e);
        }
        return null;
    }
}
