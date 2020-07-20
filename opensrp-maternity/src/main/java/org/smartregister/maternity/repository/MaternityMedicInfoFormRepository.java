package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.MaternityMedicInfoFormDao;
import org.smartregister.maternity.pojo.MaternityMedicInfoForm;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class MaternityMedicInfoFormRepository extends BaseRepository implements MaternityMedicInfoFormDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.MATERNITY_MEDIC_INFO_FORM + "("
            + MaternityDbConstants.Column.MaternityMedicInfoForm.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.MaternityMedicInfoForm.FORM + " TEXT NOT NULL, "
            + MaternityDbConstants.Column.MaternityMedicInfoForm.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM
            + "_" + MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM +
            "(" + MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{
            MaternityDbConstants.Column.MaternityMedicInfoForm.ID,
            MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID,
            MaternityDbConstants.Column.MaternityMedicInfoForm.FORM,
            MaternityDbConstants.Column.MaternityMedicInfoForm.CREATED_AT};

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull MaternityMedicInfoForm maternityMedicInfoForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID, maternityMedicInfoForm.getBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.MaternityMedicInfoForm.FORM, maternityMedicInfoForm.getForm());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(MaternityDbConstants.Column.MaternityMedicInfoForm.CREATED_AT, maternityMedicInfoForm.getCreatedAt());
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM, null, contentValues);
        return rows != -1;
    }

    @Nullable
    @Override
    public org.smartregister.maternity.pojo.MaternityMedicInfoForm findOne(@NonNull org.smartregister.maternity.pojo.MaternityMedicInfoForm maternityMedicInfoForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM
                , columns
                , MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityMedicInfoForm.getBaseEntityId()}
                , null
                , null
                , null);

        if (cursor.getCount() == 0) {
            return null;
        }

        org.smartregister.maternity.pojo.MaternityMedicInfoForm diagnosisAndTreatmentForm = null;
        if (cursor.moveToNext()) {
            diagnosisAndTreatmentForm = new org.smartregister.maternity.pojo.MaternityMedicInfoForm(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            cursor.close();
        }

        return diagnosisAndTreatmentForm;
    }

    @Override
    public boolean delete(@NonNull MaternityMedicInfoForm maternityMedicInfoForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(MaternityDbConstants.Table.MATERNITY_OUTCOME_FORM
                , MaternityDbConstants.Column.MaternityMedicInfoForm.BASE_ENTITY_ID + " = ? "
                , new String[]{maternityMedicInfoForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<MaternityMedicInfoForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
