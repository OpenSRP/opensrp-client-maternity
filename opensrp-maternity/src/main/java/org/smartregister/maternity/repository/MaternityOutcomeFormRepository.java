package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.MaternityOutcomeFormDao;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class MaternityOutcomeFormRepository extends BaseRepository implements MaternityOutcomeFormDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM + "("
            + MaternityDbConstants.Column.MaternityOutcomeForm.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.MaternityOutcomeForm.FORM + " TEXT NOT NULL, "
            + MaternityDbConstants.Column.MaternityOutcomeForm.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM
            + "_" + MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM +
            "(" + MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{
            MaternityDbConstants.Column.MaternityOutcomeForm.ID,
            MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID,
            MaternityDbConstants.Column.MaternityOutcomeForm.FORM,
            MaternityDbConstants.Column.MaternityOutcomeForm.CREATED_AT};

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.maternity.pojos.MaternityOutcomeForm maternityOutcomeForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID, maternityOutcomeForm.getBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.MaternityOutcomeForm.FORM, maternityOutcomeForm.getForm());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(MaternityDbConstants.Column.MaternityOutcomeForm.CREATED_AT, maternityOutcomeForm.getCreatedAt());
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM, null, contentValues);
        return rows != -1;
    }

    @Nullable
    @Override
    public org.smartregister.maternity.pojos.MaternityOutcomeForm findOne(@NonNull org.smartregister.maternity.pojos.MaternityOutcomeForm maternityOutcomeForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM
                , columns
                , MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityOutcomeForm.getBaseEntityId()}
                , null
                , null
                , null);

        if (cursor.getCount() == 0) {
            return null;
        }

        org.smartregister.maternity.pojos.MaternityOutcomeForm diagnosisAndTreatmentForm = null;
        if (cursor.moveToNext()) {
            diagnosisAndTreatmentForm = new org.smartregister.maternity.pojos.MaternityOutcomeForm(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            cursor.close();
        }

        return diagnosisAndTreatmentForm;
    }

    @Override
    public boolean delete(@NonNull org.smartregister.maternity.pojos.MaternityOutcomeForm maternityOutcomeForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM
                , MaternityDbConstants.Column.MaternityOutcomeForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityOutcomeForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<org.smartregister.maternity.pojos.MaternityOutcomeForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
