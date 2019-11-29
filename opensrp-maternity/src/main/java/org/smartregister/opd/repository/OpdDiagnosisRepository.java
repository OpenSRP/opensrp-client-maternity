package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdDiagnosisDao;
import org.smartregister.opd.pojos.OpdDiagnosis;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdDiagnosisRepository extends BaseRepository implements OpdDiagnosisDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_DIAGNOSIS + "("
            + OpdDbConstants.Column.OpdDiagnosis.ID + " VARCHAR NOT NULL,"
            + OpdDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.TYPE + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.DISEASE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.ICD10_CODE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.CODE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.DETAILS + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.VISIT_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.UPDATED_AT + " INTEGER NOT NULL, "
            + OpdDbConstants.Column.OpdDiagnosis.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + OpdDbConstants.Column.OpdDiagnosis.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_DIAGNOSIS
            + "_" + OpdDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_DIAGNOSIS +
            "(" + OpdDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_DIAGNOSIS
            + "_" + OpdDbConstants.Column.OpdDiagnosis.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_DIAGNOSIS +
            "(" + OpdDbConstants.Column.OpdDiagnosis.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            OpdDbConstants.Column.OpdDiagnosis.ID,
//            OpdDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID,
//            OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS,
//            OpdDbConstants.Column.OpdDiagnosis.TYPE,
//            OpdDbConstants.Column.OpdDiagnosis.DISEASE,
//            OpdDbConstants.Column.OpdDiagnosis.ICD10_CODE,
//            OpdDbConstants.Column.OpdDiagnosis.CODE,
//            OpdDbConstants.Column.OpdDiagnosis.DETAILS,
//            OpdDbConstants.Column.OpdDiagnosis.VISIT_ID,
//            OpdDbConstants.Column.OpdDiagnosis.CREATED_AT,
//            OpdDbConstants.Column.OpdDiagnosis.UPDATED_AT
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
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.ID, opdDiagnosis.getId());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.BASE_ENTITY_ID, opdDiagnosis.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS, opdDiagnosis.getDiagnosis());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.TYPE, opdDiagnosis.getType());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.DISEASE, opdDiagnosis.getDisease());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.ICD10_CODE, opdDiagnosis.getIcd10Code());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.CODE, opdDiagnosis.getCode());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.DETAILS, opdDiagnosis.getDetails());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.VISIT_ID, opdDiagnosis.getVisitId());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.CREATED_AT, opdDiagnosis.getCreatedAt());
        contentValues.put(OpdDbConstants.Column.OpdDiagnosis.UPDATED_AT, opdDiagnosis.getUpdatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_DIAGNOSIS, null, contentValues);
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
