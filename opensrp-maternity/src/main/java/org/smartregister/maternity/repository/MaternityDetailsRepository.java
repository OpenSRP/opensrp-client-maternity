package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.OpdDetailsDao;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityDbConstants.Column.OpdDetails;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityDetailsRepository extends BaseRepository implements OpdDetailsDao {

    private String[] columns = new String[]{
            OpdDetails.ID,
            OpdDetails.BASE_ENTITY_ID,
            OpdDetails.PENDING_DIAGNOSE_AND_TREAT,
            OpdDetails.CURRENT_VISIT_START_DATE,
            OpdDetails.CURRENT_VISIT_END_DATE,
            OpdDetails.CURRENT_VISIT_ID,
            OpdDetails.CREATED_AT
    };

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.OPD_DETAILS + "("
            + OpdDetails.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + OpdDetails.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDetails.PENDING_DIAGNOSE_AND_TREAT + " BOOLEAN NOT NULL, "
            + OpdDetails.CURRENT_VISIT_START_DATE + " DATETIME, "
            + OpdDetails.CURRENT_VISIT_END_DATE + " DATETIME, "
            + OpdDetails.CURRENT_VISIT_ID + " VARCHAR NOT NULL, "
            + OpdDetails.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), UNIQUE(" + OpdDetails.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    public MaternityDetailsRepository(@NonNull Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
    }

    @NonNull
    public ContentValues createValuesFor(@NonNull MaternityDetails maternityDetails) {
        ContentValues contentValues = new ContentValues();

        if (maternityDetails.getId() != 0) {
            contentValues.put(OpdDetails.ID, maternityDetails.getId());
        }

        contentValues.put(OpdDetails.BASE_ENTITY_ID, maternityDetails.getBaseEntityId());
        contentValues.put(OpdDetails.PENDING_DIAGNOSE_AND_TREAT, maternityDetails.isPendingDiagnoseAndTreat());

        if (maternityDetails.getCurrentVisitStartDate() != null) {
            contentValues.put(OpdDetails.CURRENT_VISIT_START_DATE, MaternityUtils.convertDate(maternityDetails.getCurrentVisitStartDate(), MaternityDbConstants.DATE_FORMAT));
        }

        if (maternityDetails.getCurrentVisitEndDate() != null) {
            contentValues.put(OpdDetails.CURRENT_VISIT_END_DATE, MaternityUtils.convertDate(maternityDetails.getCurrentVisitEndDate(), MaternityDbConstants.DATE_FORMAT));
        }

        contentValues.put(OpdDetails.CURRENT_VISIT_ID, maternityDetails.getCurrentVisitId());
        return contentValues;
    }

    @Override
    public boolean saveOrUpdate(@NonNull MaternityDetails maternityDetails) {
        ContentValues contentValues = createValuesFor(maternityDetails);

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertWithOnConflict(MaternityDbConstants.Table.OPD_DETAILS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return recordId != -1;
    }

    @Nullable
    @Override
    public MaternityDetails findOne(@NonNull MaternityDetails maternityDetails) {
        MaternityDetails details = null;
        if (maternityDetails.getCurrentVisitId() != null && maternityDetails.getBaseEntityId() != null) {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(MaternityDbConstants.Table.OPD_DETAILS, columns, OpdDetails.BASE_ENTITY_ID + "=? and " + OpdDetails.CURRENT_VISIT_ID + "=?",
                    new String[]{maternityDetails.getBaseEntityId(), maternityDetails.getCurrentVisitId()}, null, null, null, "1");
            if (cursor.getCount() == 0) {
                return null;
            }

            if (cursor.moveToNext()) {
                details = new MaternityDetails();
                details.setId(cursor.getInt(0));
                details.setBaseEntityId(cursor.getString(1));
                details.setPendingDiagnoseAndTreat((cursor.getInt(2) == 1));

                details.setCurrentVisitStartDate(MaternityUtils
                        .convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS,
                                cursor.getString(3)));
                details.setCurrentVisitEndDate(MaternityUtils
                        .convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS,
                                cursor.getString(4)));
                details.setCurrentVisitId(cursor.getString(5));
                details.setCreatedAt(MaternityUtils
                        .convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS,
                                cursor.getString(6)));
                cursor.close();
            }

        }
        return details;
    }

    @Override
    public boolean delete(MaternityDetails maternityDetails) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<MaternityDetails> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
