package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.dao.OpdVisitDao;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdDbConstants.Column.OpdVisit;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.text.ParseException;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-01
 */

public class OpdVisitRepository extends BaseRepository implements OpdVisitDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_VISIT + "("
            + OpdVisit.ID + " VARCHAR NOT NULL PRIMARY KEY,"
            + OpdVisit.VISIT_DATE + " DATETIME NOT NULL,"
            + OpdVisit.PROVIDER_ID + " VARCHAR NOT NULL,"
            + OpdVisit.LOCATION_ID + " VARCHAR NOT NULL,"
            + OpdVisit.BASE_ENTITY_ID + " VARCHAR NOT NULL,"
            + OpdVisit.CREATED_AT + " INTEGER NOT NULL)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_VISIT
            + "_" + OpdVisit.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_VISIT + "(" + OpdVisit.BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String INDEX_VISIT_DATE = "CREATE INDEX " + OpdDbConstants.Table.OPD_VISIT
            + "_" + OpdVisit.VISIT_DATE + "_index ON " + OpdDbConstants.Table.OPD_VISIT + "(" + OpdVisit.VISIT_DATE + " COLLATE NOCASE);";

    private String[] columns = new String[]{OpdVisit.ID
            , OpdVisit.VISIT_DATE
            , OpdVisit.PROVIDER_ID
            , OpdVisit.LOCATION_ID
            , OpdVisit.BASE_ENTITY_ID
            , OpdVisit.CREATED_AT};

    public OpdVisitRepository(Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_DATE);
    }


    public ContentValues createValuesFor(@NonNull org.smartregister.opd.pojos.OpdVisit visit) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(OpdVisit.ID, visit.getId());
        contentValues.put(OpdVisit.VISIT_DATE, dateFormat.format(visit.getVisitDate()));
        contentValues.put(OpdVisit.PROVIDER_ID, visit.getProviderId());
        contentValues.put(OpdVisit.LOCATION_ID, visit.getLocationId());
        contentValues.put(OpdVisit.BASE_ENTITY_ID, visit.getBaseEntityId());
        contentValues.put(OpdVisit.CREATED_AT, visit.getVisitDate().getTime());

        return contentValues;
    }

    @NonNull
    protected org.smartregister.opd.pojos.OpdVisit getVisitResult(@NonNull Cursor cursor) {
        org.smartregister.opd.pojos.OpdVisit visit = new org.smartregister.opd.pojos.OpdVisit();
        visit.setId(cursor.getString(cursor.getColumnIndex(OpdVisit.ID)));

        try {
            visit.setVisitDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(OpdVisit.VISIT_DATE))));
        } catch (ParseException ex) {
            Timber.e(ex);
        }

        visit.setProviderId(cursor.getString(cursor.getColumnIndex(OpdVisit.PROVIDER_ID)));
        visit.setLocationId(cursor.getString(cursor.getColumnIndex(OpdVisit.LOCATION_ID)));
        visit.setBaseEntityId(cursor.getString(cursor.getColumnIndex(OpdVisit.BASE_ENTITY_ID)));
        visit.setCreatedAt(new Date(cursor.getLong(cursor.getColumnIndex(OpdVisit.CREATED_AT))));

        return visit;
    }

    @Nullable
    @Override
    public org.smartregister.opd.pojos.OpdVisit getLatestVisit(@NonNull String clientBaseEntityId) {

        Cursor mCursor = null;
        org.smartregister.opd.pojos.OpdVisit visit = null;
        try {
            SQLiteDatabase db = getWritableDatabase();

            if (StringUtils.isNotBlank(clientBaseEntityId)) {
                mCursor = db.query(OpdDbConstants.Table.OPD_VISIT, columns, OpdVisit.BASE_ENTITY_ID + " = ?"
                        , new String[]{clientBaseEntityId}
                        , null
                        , null
                        , OpdVisit.VISIT_DATE + " DESC"
                        , "1");

                if (mCursor != null && mCursor.moveToNext()) {
                    visit = getVisitResult(mCursor);
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return visit;
    }

    public boolean addVisit(@NonNull org.smartregister.opd.pojos.OpdVisit visit) {
        ContentValues contentValues = createValuesFor(visit);

        //TODO: Check for duplicates

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insert(OpdDbConstants.Table.OPD_VISIT, null, contentValues);

        return recordId != -1;
    }
}
