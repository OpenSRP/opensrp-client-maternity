package org.smartregister.maternity.repository;

import android.content.ContentValues;
import androidx.annotation.Nullable;

import net.sqlcipher.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.BaseRepository;

import java.util.Date;

import timber.log.Timber;

public class MaternityRepository extends BaseRepository {

    public void updateLastInteractedWith(@Nullable String baseEntityId) {
        try {
            if (StringUtils.isNotBlank(baseEntityId)) {
                String tableName = MaternityUtils.metadata().getTableName();

                String lastInteractedWithDate = String.valueOf(new Date().getTime());

                ContentValues contentValues = new ContentValues();
                contentValues.put(MaternityConstants.JSON_FORM_KEY.LAST_INTERACTED_WITH, lastInteractedWithDate);

                getWritableDatabase()
                        .update(tableName, contentValues,
                                String.format("%s = ?", MaternityConstants.KEY.BASE_ENTITY_ID),
                                new String[]{baseEntityId});

                // Update FTS
                CommonRepository commonrepository = MaternityLibrary
                        .getInstance()
                        .context()
                        .commonrepository(tableName);

                if (commonrepository.isFts()) {
                    getWritableDatabase()
                            .update(CommonFtsObject.searchTableName(tableName), contentValues,
                                    CommonFtsObject.idColumn + " = ?", new String[]{baseEntityId});
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            Timber.e(e);
        }
    }
}
