package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.OpdDiagnosisDao;
import org.smartregister.maternity.pojos.OpdDiagnosis;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdDiagnosisRepository extends BaseRepository implements OpdDiagnosisDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.OPD_DIAGNOSIS + "("
            + MaternityDbConstants.Column.OpdDiagnosis.ID + " VARCHAR NOT NULL,"
            + MaternityDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.DIAGNOSIS + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.TYPE + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.DISEASE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.ICD10_CODE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.CODE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.DETAILS + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.VISIT_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.UPDATED_AT + " INTEGER NOT NULL, "
            + MaternityDbConstants.Column.OpdDiagnosis.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + MaternityDbConstants.Column.OpdDiagnosis.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_DIAGNOSIS
            + "_" + MaternityDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.OPD_DIAGNOSIS +
            "(" + MaternityDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_DIAGNOSIS
            + "_" + MaternityDbConstants.Column.OpdDiagnosis.VISIT_ID + "_index ON " + MaternityDbConstants.Table.OPD_DIAGNOSIS +
            "(" + MaternityDbConstants.Column.OpdDiagnosis.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            MaternityDbConstants.Column.OpdDiagnosis.ID,
//            MaternityDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID,
//            MaternityDbConstants.Column.OpdDiagnosis.DIAGNOSIS,
//            MaternityDbConstants.Column.OpdDiagnosis.TYPE,
//            MaternityDbConstants.Column.OpdDiagnosis.DISEASE,
//            MaternityDbConstants.Column.OpdDiagnosis.ICD10_CODE,
//            MaternityDbConstants.Column.OpdDiagnosis.CODE,
//            MaternityDbConstants.Column.OpdDiagnosis.DETAILS,
//            MaternityDbConstants.Column.OpdDiagnosis.VISIT_ID,
//            MaternityDbConstants.Column.OpdDiagnosis.CREATED_AT,
//            MaternityDbConstants.Column.OpdDiagnosis.UPDATED_AT
//    };

    public OpdDiagnosisRepository(@NonNull Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull OpdDiagnosis opdDiagnosis) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.ID, opdDiagnosis.getId());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID, opdDiagnosis.getBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.DIAGNOSIS, opdDiagnosis.getDiagnosis());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.TYPE, opdDiagnosis.getType());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.DISEASE, opdDiagnosis.getDisease());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.ICD10_CODE, opdDiagnosis.getIcd10Code());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.CODE, opdDiagnosis.getCode());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.DETAILS, opdDiagnosis.getDetails());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.VISIT_ID, opdDiagnosis.getVisitId());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.CREATED_AT, opdDiagnosis.getCreatedAt());
        contentValues.put(MaternityDbConstants.Column.OpdDiagnosis.UPDATED_AT, opdDiagnosis.getUpdatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.OPD_DIAGNOSIS, null, contentValues);
        return rows != -1;
    }

    @Override
    public OpdDiagnosis findOne(OpdDiagnosis opdDiagnosis) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(OpdDiagnosis opdDiagnosis) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<OpdDiagnosis> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
