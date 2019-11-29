package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.dao.OpdCheckInDao;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdDbConstants.Column.OpdCheckIn;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */

public class OpdCheckInRepository extends BaseRepository implements OpdCheckInDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_CHECK_IN + "("
            + OpdCheckIn.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + OpdCheckIn.FORM_SUBMISSION_ID + " VARCHAR NOT NULL, "
            + OpdCheckIn.VISIT_ID + " VARCHAR NOT NULL, "
            + OpdCheckIn.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdCheckIn.PREGNANCY_STATUS + " VARCHAR, "
            + OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY + " VARCHAR, "
            + OpdCheckIn.HIV_RESULTS_PREVIOUSLY + " VARCHAR, "
            + OpdCheckIn.IS_TAKING_ART + " VARCHAR, "
            + OpdCheckIn.CURRENT_HIV_RESULT + " VARCHAR, "
            + OpdCheckIn.VISIT_TYPE + " VARCHAR NOT NULL, "
            + OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY + " VARCHAR, "
            + OpdCheckIn.APPOINTMENT_DUE_DATE + " INTEGER, "
            + OpdCheckIn.CREATED_AT + " INTEGER NOT NULL, "
            + OpdCheckIn.UPDATED_AT + " INTEGER NOT NULL, UNIQUE(" + OpdCheckIn.VISIT_ID + ", " + OpdCheckIn.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_CHECK_IN
            + "_" + OpdCheckIn.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_CHECK_IN + "(" + OpdCheckIn.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_CHECK_IN
            + "_" + OpdCheckIn.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_CHECK_IN + "(" + OpdCheckIn.VISIT_ID + ");";
    private static final String INDEX_EVENT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_CHECK_IN
            + "_" + OpdCheckIn.FORM_SUBMISSION_ID + "_index ON " + OpdDbConstants.Table.OPD_CHECK_IN + "(" + OpdCheckIn.FORM_SUBMISSION_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{OpdCheckIn.ID
            , OpdCheckIn.FORM_SUBMISSION_ID
            , OpdCheckIn.VISIT_ID
            , OpdCheckIn.BASE_ENTITY_ID
            , OpdCheckIn.PREGNANCY_STATUS
            , OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY
            , OpdCheckIn.HIV_RESULTS_PREVIOUSLY
            , OpdCheckIn.IS_TAKING_ART
            , OpdCheckIn.CURRENT_HIV_RESULT
            , OpdCheckIn.VISIT_TYPE
            , OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY
            , OpdCheckIn.APPOINTMENT_DUE_DATE
            , OpdCheckIn.CREATED_AT
            , OpdCheckIn.UPDATED_AT
    };

    public OpdCheckInRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
        database.execSQL(INDEX_EVENT_ID);
    }

    public ContentValues createValuesFor(@NonNull org.smartregister.opd.pojos.OpdCheckIn checkIn) {
        ContentValues contentValues = new ContentValues();

        if (checkIn.getId() != 0) {
            contentValues.put(OpdCheckIn.ID, checkIn.getId());
        }

        contentValues.put(OpdCheckIn.FORM_SUBMISSION_ID, checkIn.getFormSubmissionId());
        contentValues.put(OpdCheckIn.VISIT_ID, checkIn.getVisitId());
        contentValues.put(OpdCheckIn.BASE_ENTITY_ID, checkIn.getBaseEntityId());
        contentValues.put(OpdCheckIn.PREGNANCY_STATUS, checkIn.getPregnancyStatus());
        contentValues.put(OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY, checkIn.getHasHivTestPreviously());
        contentValues.put(OpdCheckIn.HIV_RESULTS_PREVIOUSLY, checkIn.getHivResultsPreviously());
        contentValues.put(OpdCheckIn.IS_TAKING_ART, checkIn.getIsTakingArt());
        contentValues.put(OpdCheckIn.CURRENT_HIV_RESULT, checkIn.getCurrentHivResult());
        contentValues.put(OpdCheckIn.VISIT_TYPE, checkIn.getVisitType());
        contentValues.put(OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY, checkIn.getAppointmentScheduledPreviously());
        contentValues.put(OpdCheckIn.APPOINTMENT_DUE_DATE, checkIn.getAppointmentDueDate());
        contentValues.put(OpdCheckIn.CREATED_AT, checkIn.getCreatedAt());
        contentValues.put(OpdCheckIn.UPDATED_AT, checkIn.getUpdatedAt());

        return contentValues;
    }

    @Nullable
    @Override
    public org.smartregister.opd.pojos.OpdCheckIn getLatestCheckIn(@NonNull String clientBaseEntityId) {
        Cursor mCursor = null;
        org.smartregister.opd.pojos.OpdCheckIn checkIn = null;
        try {
            SQLiteDatabase db = getWritableDatabase();

            if (StringUtils.isNotBlank(clientBaseEntityId)) {
                mCursor = db.query(OpdDbConstants.Table.OPD_CHECK_IN, columns, OpdCheckIn.BASE_ENTITY_ID + " = ?"
                        , new String[]{clientBaseEntityId}
                        , null
                        , null
                        , OpdCheckIn.CREATED_AT + " DESC"
                        , "1");

                if (mCursor != null && mCursor.moveToNext()) {
                    checkIn = getCheckInResult(mCursor);
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return checkIn;
    }

    @Nullable
    @Override
    public org.smartregister.opd.pojos.OpdCheckIn getCheckInByVisit(@NonNull String visitId) {
        Cursor mCursor = null;
        org.smartregister.opd.pojos.OpdCheckIn checkIn = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            mCursor = db.query(OpdDbConstants.Table.OPD_CHECK_IN, columns, OpdCheckIn.VISIT_ID + " = ?"
                    , new String[]{visitId}
                    , null
                    , null
                    , OpdCheckIn.CREATED_AT + " DESC"
                    , "1");

            if (mCursor != null && mCursor.moveToNext()) {
                checkIn = getCheckInResult(mCursor);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return checkIn;
    }

    @NonNull
    protected org.smartregister.opd.pojos.OpdCheckIn getCheckInResult(@NonNull Cursor cursor) {
        org.smartregister.opd.pojos.OpdCheckIn checkIn = new org.smartregister.opd.pojos.OpdCheckIn();

        checkIn.setId(cursor.getInt(cursor.getColumnIndex(OpdCheckIn.ID)));
        checkIn.setFormSubmissionId(cursor.getString(cursor.getColumnIndex(OpdCheckIn.FORM_SUBMISSION_ID)));
        checkIn.setVisitId(cursor.getString(cursor.getColumnIndex(OpdCheckIn.VISIT_ID)));
        checkIn.setBaseEntityId(cursor.getString(cursor.getColumnIndex(OpdCheckIn.BASE_ENTITY_ID)));
        checkIn.setPregnancyStatus(cursor.getString(cursor.getColumnIndex(OpdCheckIn.PREGNANCY_STATUS)));
        checkIn.setHasHivTestPreviously(cursor.getString(cursor.getColumnIndex(OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY)));
        checkIn.setHivResultsPreviously(cursor.getString(cursor.getColumnIndex(OpdCheckIn.HIV_RESULTS_PREVIOUSLY)));
        checkIn.setIsTakingArt(cursor.getString(cursor.getColumnIndex(OpdCheckIn.IS_TAKING_ART)));
        checkIn.setCurrentHivResult(cursor.getString(cursor.getColumnIndex(OpdCheckIn.CURRENT_HIV_RESULT)));
        checkIn.setVisitType(cursor.getString(cursor.getColumnIndex(OpdCheckIn.VISIT_TYPE)));
        checkIn.setAppointmentScheduledPreviously(cursor.getString(cursor.getColumnIndex(OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY)));
        checkIn.setAppointmentDueDate(cursor.getString(cursor.getColumnIndex(OpdCheckIn.APPOINTMENT_DUE_DATE)));
        checkIn.setCreatedAt(cursor.getInt(cursor.getColumnIndex(OpdCheckIn.CREATED_AT)));
        checkIn.setUpdatedAt(cursor.getInt(cursor.getColumnIndex(OpdCheckIn.UPDATED_AT)));

        return checkIn;
    }

    @Override
    public boolean addCheckIn(@NonNull org.smartregister.opd.pojos.OpdCheckIn checkIn) throws SQLiteException {
        ContentValues contentValues = createValuesFor(checkIn);

        //TODO: Check for duplicates

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertOrThrow(OpdDbConstants.Table.OPD_CHECK_IN, null, contentValues);

        return recordId != -1;
    }
}
