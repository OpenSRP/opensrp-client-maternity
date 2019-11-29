package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdDiagnosisAndTreatmentFormDao;
import org.smartregister.opd.pojos.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdDiagnosisAndTreatmentFormRepository extends BaseRepository implements OpdDiagnosisAndTreatmentFormDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_DIAGNOSIS_AND_TREATMENT_FORM + "("
            + OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.FORM + " TEXT NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_DIAGNOSIS_AND_TREATMENT_FORM
            + "_" + OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_DIAGNOSIS_AND_TREATMENT_FORM +
            "(" + OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{
            OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.ID,
            OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID,
            OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.FORM,
            OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.CREATED_AT};

    public OpdDiagnosisAndTreatmentFormRepository(@NonNull Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID, opdDiagnosisAndTreatmentForm.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.FORM, opdDiagnosisAndTreatmentForm.getForm());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        contentValues.put(OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.CREATED_AT, opdDiagnosisAndTreatmentForm.getCreatedAt());
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_DIAGNOSIS_AND_TREATMENT_FORM, null, contentValues);
        return rows != -1;
    }

    @Nullable
    @Override
    public OpdDiagnosisAndTreatmentForm findOne(@NonNull OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(OpdDbConstants.Table.OPD_DIAGNOSIS_AND_TREATMENT_FORM
                , columns
                , OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID + " = ? "
                , new String[]{opdDiagnosisAndTreatmentForm.getBaseEntityId()}
                , null
                , null
                , null);

        if (cursor.getCount() == 0) {
            return null;
        }

        OpdDiagnosisAndTreatmentForm diagnosisAndTreatmentForm = null;
        if (cursor.moveToNext()) {
            diagnosisAndTreatmentForm = new OpdDiagnosisAndTreatmentForm(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            cursor.close();
        }

        return diagnosisAndTreatmentForm;
    }

    @Override
    public boolean delete(@NonNull OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int rows = sqLiteDatabase.delete(OpdDbConstants.Table.OPD_DIAGNOSIS_AND_TREATMENT_FORM
                , OpdDbConstants.Column.OpdDiagnosisAndTreatmentForm.BASE_ENTITY_ID + " = ? "
                , new String[]{opdDiagnosisAndTreatmentForm.getBaseEntityId()});

        return rows > 0;
    }

    @Override
    public List<OpdDiagnosisAndTreatmentForm> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
