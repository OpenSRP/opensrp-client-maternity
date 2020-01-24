package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.OpdDetailsDao;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityDbConstants.Column.MaternityDetails;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.BaseRepository;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityDetailsRepository extends BaseRepository implements OpdDetailsDao {

    private String[] columns = new String[]{
            MaternityDetails.ID,
            MaternityDetails.BASE_ENTITY_ID,
            MaternityDetails.PENDING_OUTCOME,
            MaternityDetails.CONCEPTION_DATE,
            MaternityDetails.CREATED_AT
    };

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.MATERNITY_DETAILS + "("
            + MaternityDetails.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + MaternityDetails.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDetails.PENDING_OUTCOME + " BOOLEAN NOT NULL, "
            // TODO: Make conception_date NOT NULL
            + MaternityDetails.CONCEPTION_DATE + " VARCHAR, "
            + MaternityDetails.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), UNIQUE(" + MaternityDetails.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
    }

    @NonNull
    public ContentValues createValuesFor(@NonNull org.smartregister.maternity.pojos.MaternityDetails maternityDetails) {
        ContentValues contentValues = new ContentValues();

        if (maternityDetails.getId() != 0) {
            contentValues.put(MaternityDetails.ID, maternityDetails.getId());
        }

        contentValues.put(MaternityDetails.BASE_ENTITY_ID, maternityDetails.getBaseEntityId());
        contentValues.put(MaternityDetails.PENDING_OUTCOME, maternityDetails.isPendingOutcome());
        contentValues.put(MaternityDetails.CONCEPTION_DATE, maternityDetails.getConceptionDate());

        return contentValues;
    }

    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.maternity.pojos.MaternityDetails maternityDetails) {
        ContentValues contentValues = createValuesFor(maternityDetails);

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertWithOnConflict(MaternityDbConstants.Table.MATERNITY_DETAILS, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return recordId != -1;
    }

    @Nullable
    @Override
    public org.smartregister.maternity.pojos.MaternityDetails findOne(@NonNull org.smartregister.maternity.pojos.MaternityDetails maternityDetails) {
        org.smartregister.maternity.pojos.MaternityDetails details = null;
       /* if (maternityDetails.getCurrentVisitId() != null && maternityDetails.getBaseEntityId() != null) {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query(MaternityDbConstants.Table.MATERNITY_DETAILS, columns, MaternityDetails.BASE_ENTITY_ID + "=? and " + MaternityDetails.CURRENT_VISIT_ID + "=?",
                    new String[]{maternityDetails.getBaseEntityId(), maternityDetails.getCurrentVisitId()}, null, null, null, "1");
            if (cursor.getCount() == 0) {
                return null;
            }

            if (cursor.moveToNext()) {
                details = new org.smartregister.maternity.pojos.MaternityDetails();
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

        }*/
        return details;
    }

    @Override
    public boolean delete(org.smartregister.maternity.pojos.MaternityDetails maternityDetails) {
        throw new NotImplementedException("Not Implemented");
    }

    public boolean delete(@NonNull String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rowsDeleted = sqLiteDatabase.delete(MaternityDbConstants.Table.MATERNITY_DETAILS, MaternityDetails.BASE_ENTITY_ID + " = ?", new String[]{baseEntityId});

        return rowsDeleted > 0;
    }

    @Override
    public List<org.smartregister.maternity.pojos.MaternityDetails> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
