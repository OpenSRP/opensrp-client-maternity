package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.MaternityDetailsDao;
import org.smartregister.maternity.pojos.MaternityBaseDetails;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.BaseRepository;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public abstract class MaternityDetailsRepository extends BaseRepository implements MaternityDetailsDao {

    private String[] columns;

    public abstract String getTableName();

    @NonNull
    public ContentValues createValuesFor(@NonNull MaternityBaseDetails maternityDetails) {
        ContentValues contentValues = new ContentValues();
        if (maternityDetails.getId() != 0) {
            contentValues.put(MaternityDbConstants.Column.MaternityDetails.ID, maternityDetails.getId());
        }

        if (maternityDetails.getCreatedAt() != null) {
            contentValues.put(MaternityDbConstants.Column.MaternityDetails.CREATED_AT, MaternityUtils.convertDate(maternityDetails.getCreatedAt(), MaternityDbConstants.DATE_FORMAT));
        }

        if (maternityDetails.getEventDate() != null) {
            contentValues.put(MaternityDbConstants.Column.MaternityDetails.EVENT_DATE, MaternityUtils.convertDate(maternityDetails.getEventDate(), MaternityDbConstants.DATE_FORMAT));
        }

        contentValues.put(MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID, maternityDetails.getBaseEntityId());
        for (String column: getPropertyNames()) {
            contentValues.put(column, maternityDetails.getProperties().get(column));
        }

        return contentValues;
    }

    @Override
    public boolean saveOrUpdate(@NonNull MaternityBaseDetails maternityDetails) {
        ContentValues contentValues = createValuesFor(maternityDetails);

        SQLiteDatabase database = getWritableDatabase();
        long recordId = database.insertWithOnConflict(getTableName(), null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return recordId != -1;
    }

    @Nullable
    @Override
    public MaternityBaseDetails findOne(@NonNull MaternityBaseDetails maternityDetails) {
        MaternityBaseDetails details = null;
        if (maternityDetails.getBaseEntityId() != null) {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();

            Cursor cursor = sqLiteDatabase.query(getTableName(), getColumns(), MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + " = ?",
                    new String[]{maternityDetails.getBaseEntityId()}, null, null, null, "1");
            if (cursor.getCount() == 0) {
                return null;
            }

            if (cursor.moveToNext()) {
                details = convert(cursor);
                cursor.close();
            }

        }
        return details;
    }

    public MaternityBaseDetails convert(@NonNull Cursor cursor) {
        MaternityBaseDetails maternityDetails = new MaternityBaseDetails();

        maternityDetails.setId(cursor.getInt(cursor.getColumnIndex(MaternityDbConstants.Column.MaternityDetails.ID)));
        maternityDetails.setBaseEntityId(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID)));
        maternityDetails.setEventDate(MaternityUtils.convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.MaternityDetails.EVENT_DATE))));
        maternityDetails.setCreatedAt(MaternityUtils.convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.MaternityDetails.CREATED_AT))));

        for (String column: getPropertyNames()) {
            int colIndex = cursor.getColumnIndex(column);
            if (colIndex != -1) {
                maternityDetails.put(column, cursor.getString(colIndex));
            }
        }

        return maternityDetails;
    }

    public abstract String[] getPropertyNames();

    @Override
    public boolean delete(MaternityBaseDetails maternityDetails) {
        throw new NotImplementedException("Not Implemented");
    }

    public boolean delete(@NonNull String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rowsDeleted = sqLiteDatabase.delete(getTableName(), MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + " = ?", new String[]{baseEntityId});

        return rowsDeleted > 0;
    }

    @Override
    public List<MaternityBaseDetails> findAll() {
        throw new NotImplementedException("Not Implemented");
    }

    public String[] getColumns() {
        if (this.columns == null) {
            String[] propertyNames = getPropertyNames();
            String[] columns = new String[propertyNames.length + 4];

            columns[0] = MaternityDbConstants.Column.MaternityDetails.ID;
            columns[1] = MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID;
            columns[2] = MaternityDbConstants.Column.MaternityDetails.CREATED_AT;
            columns[3] = MaternityDbConstants.Column.MaternityDetails.EVENT_DATE;

            for (int i = 0; i < propertyNames.length; i++) {
                columns[i + 4] = propertyNames[i];
            }
            this.columns = columns;
        }

        return this.columns;

    }
}
