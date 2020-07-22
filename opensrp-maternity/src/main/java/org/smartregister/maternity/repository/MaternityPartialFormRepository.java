package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.MaternityPartialFormDao;
import org.smartregister.maternity.pojo.MaternityPartialForm;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class MaternityPartialFormRepository extends BaseRepository implements MaternityPartialFormDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.MATERNITY_PARTIAL_FORM + "("
            + MaternityDbConstants.Column.MaternityPartialForm.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.MaternityPartialForm.FORM + " TEXT NOT NULL, "
            + MaternityDbConstants.Column.MaternityPartialForm.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.MATERNITY_PARTIAL_FORM
            + "_" + MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.MATERNITY_PARTIAL_FORM +
            "(" + MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{
            MaternityDbConstants.Column.MaternityPartialForm.ID,
            MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID,
            MaternityDbConstants.Column.MaternityPartialForm.FORM,
            MaternityDbConstants.Column.MaternityPartialForm.CREATED_AT};

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull MaternityPartialForm maternityPartialForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID, maternityPartialForm.getBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.MaternityPartialForm.FORM, maternityPartialForm.getForm());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(MaternityDbConstants.Column.MaternityPartialForm.CREATED_AT, maternityPartialForm.getCreatedAt());
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.MATERNITY_PARTIAL_FORM, null, contentValues);
        return rows != -1;
    }

    @Nullable
    @Override
    public MaternityPartialForm findOne(@NonNull MaternityPartialForm maternityPartialForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MaternityDbConstants.Table.MATERNITY_PARTIAL_FORM
                , columns
                , MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityPartialForm.getBaseEntityId()}
                , null
                , null
                , null);

        if (cursor.getCount() == 0) {
            return null;
        }

        MaternityPartialForm diagnosisAndTreatmentForm = null;
        if (cursor.moveToNext()) {
            diagnosisAndTreatmentForm = new MaternityPartialForm(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            cursor.close();
        }

        return diagnosisAndTreatmentForm;
    }

    @Override
    public boolean delete(@NonNull MaternityPartialForm maternityPartialForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(MaternityDbConstants.Table.MATERNITY_PARTIAL_FORM
                , MaternityDbConstants.Column.MaternityPartialForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityPartialForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<MaternityPartialForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
