package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.OpdTreatmentDao;
import org.smartregister.maternity.pojos.OpdTreatment;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdTreatmentRepository extends BaseRepository implements OpdTreatmentDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.OPD_TREATMENT + "("
            + MaternityDbConstants.Column.OpdTreatment.ID + " VARCHAR NOT NULL,"
            + MaternityDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdTreatment.MEDICINE + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdTreatment.DOSAGE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdTreatment.DURATION + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdTreatment.NOTE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdTreatment.VISIT_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdTreatment.UPDATED_AT + " INTEGER NOT NULL, "
            + MaternityDbConstants.Column.OpdTreatment.CREATED_AT + " INTEGER NOT NULL ,"
            + MaternityDbConstants.Column.OpdTreatment.PROPERTY + " VARCHAR NOT NULL ," +
            "UNIQUE(" + MaternityDbConstants.Column.OpdTreatment.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_TREATMENT
            + "_" + MaternityDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.OPD_TREATMENT +
            "(" + MaternityDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_TREATMENT
            + "_" + MaternityDbConstants.Column.OpdTreatment.VISIT_ID + "_index ON " + MaternityDbConstants.Table.OPD_TREATMENT +
            "(" + MaternityDbConstants.Column.OpdTreatment.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            MaternityDbConstants.Column.OpdTreatment.ID,
//            MaternityDbConstants.Column.OpdTreatment.BASE_ENTITY_ID,
//            MaternityDbConstants.Column.OpdTreatment.MEDICINE,
//            MaternityDbConstants.Column.OpdTreatment.DOSAGE,
//            MaternityDbConstants.Column.OpdTreatment.DURATION,
//            MaternityDbConstants.Column.OpdTreatment.NOTE,
//            MaternityDbConstants.Column.OpdTreatment.VISIT_ID,
//            MaternityDbConstants.Column.OpdTreatment.UPDATED_AT,
//            MaternityDbConstants.Column.OpdTreatment.CREATED_AT
//    };

    public OpdTreatmentRepository(@NonNull Repository repository) {
        super(repository);
    }


    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull OpdTreatment opdTreatment) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.ID, opdTreatment.getId());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.BASE_ENTITY_ID, opdTreatment.getBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.MEDICINE, opdTreatment.getMedicine());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.DOSAGE, opdTreatment.getDosage());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.DURATION, opdTreatment.getDuration());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.NOTE, opdTreatment.getNote());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.VISIT_ID, opdTreatment.getVisitId());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.PROPERTY, opdTreatment.getProperty());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.CREATED_AT, opdTreatment.getCreatedAt());
        contentValues.put(MaternityDbConstants.Column.OpdTreatment.UPDATED_AT, opdTreatment.getUpdatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.OPD_TREATMENT, null, contentValues);
        return rows != -1;
    }

    @Override
    public OpdTreatment findOne(OpdTreatment opdTreatmentDao) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(OpdTreatment opdTreatmentDao) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<OpdTreatment> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
