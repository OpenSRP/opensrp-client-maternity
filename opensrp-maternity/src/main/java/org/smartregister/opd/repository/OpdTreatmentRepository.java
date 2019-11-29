package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdTreatmentDao;
import org.smartregister.opd.pojos.OpdTreatment;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdTreatmentRepository extends BaseRepository implements OpdTreatmentDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_TREATMENT + "("
            + OpdDbConstants.Column.OpdTreatment.ID + " VARCHAR NOT NULL,"
            + OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTreatment.MEDICINE + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTreatment.DOSAGE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.DURATION + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.NOTE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdTreatment.VISIT_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTreatment.UPDATED_AT + " INTEGER NOT NULL, "
            + OpdDbConstants.Column.OpdTreatment.CREATED_AT + " INTEGER NOT NULL ,"
            + OpdDbConstants.Column.OpdTreatment.PROPERTY + " VARCHAR NOT NULL ," +
            "UNIQUE(" + OpdDbConstants.Column.OpdTreatment.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TREATMENT
            + "_" + OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_TREATMENT +
            "(" + OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TREATMENT
            + "_" + OpdDbConstants.Column.OpdTreatment.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_TREATMENT +
            "(" + OpdDbConstants.Column.OpdTreatment.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            OpdDbConstants.Column.OpdTreatment.ID,
//            OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID,
//            OpdDbConstants.Column.OpdTreatment.MEDICINE,
//            OpdDbConstants.Column.OpdTreatment.DOSAGE,
//            OpdDbConstants.Column.OpdTreatment.DURATION,
//            OpdDbConstants.Column.OpdTreatment.NOTE,
//            OpdDbConstants.Column.OpdTreatment.VISIT_ID,
//            OpdDbConstants.Column.OpdTreatment.UPDATED_AT,
//            OpdDbConstants.Column.OpdTreatment.CREATED_AT
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
        contentValues.put(OpdDbConstants.Column.OpdTreatment.ID, opdTreatment.getId());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.BASE_ENTITY_ID, opdTreatment.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.MEDICINE, opdTreatment.getMedicine());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.DOSAGE, opdTreatment.getDosage());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.DURATION, opdTreatment.getDuration());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.NOTE, opdTreatment.getNote());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.VISIT_ID, opdTreatment.getVisitId());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.PROPERTY, opdTreatment.getProperty());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.CREATED_AT, opdTreatment.getCreatedAt());
        contentValues.put(OpdDbConstants.Column.OpdTreatment.UPDATED_AT, opdTreatment.getUpdatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_TREATMENT, null, contentValues);
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
