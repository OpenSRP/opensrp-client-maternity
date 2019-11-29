package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdTestConductedDao;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdTestConductedRepository extends BaseRepository implements OpdTestConductedDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_TEST_CONDUCTED + "("
            + OpdDbConstants.Column.OpdTestConducted.ID + " VARCHAR NOT NULL,"
            + OpdDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTestConducted.TEST + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTestConducted.RESULT + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTestConducted.VISIT_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdTestConducted.UPDATED_AT + " INTEGER NOT NULL, "
            + OpdDbConstants.Column.OpdTestConducted.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + OpdDbConstants.Column.OpdTestConducted.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TEST_CONDUCTED
            + "_" + OpdDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_TEST_CONDUCTED +
            "(" + OpdDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_TEST_CONDUCTED
            + "_" + OpdDbConstants.Column.OpdTestConducted.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_TEST_CONDUCTED +
            "(" + OpdDbConstants.Column.OpdTestConducted.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            OpdDbConstants.Column.OpdTestConducted.ID,
//            OpdDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID,
//            OpdDbConstants.Column.OpdTestConducted.TEST,
//            OpdDbConstants.Column.OpdTestConducted.RESULT,
//            OpdDbConstants.Column.OpdTestConducted.VISIT_ID,
//            OpdDbConstants.Column.OpdTestConducted.UPDATED_AT,
//            OpdDbConstants.Column.OpdTestConducted.CREATED_AT
//    };


    public OpdTestConductedRepository(@NonNull Repository repository) {
        super(repository);
    }


    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }


    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.opd.pojos.OpdTestConducted opdTestConducted) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdDbConstants.Column.OpdTestConducted.ID, opdTestConducted.getId());
        contentValues.put(OpdDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID, opdTestConducted.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdTestConducted.TEST, opdTestConducted.getTest());
        contentValues.put(OpdDbConstants.Column.OpdTestConducted.RESULT, opdTestConducted.getResult());
        contentValues.put(OpdDbConstants.Column.OpdTestConducted.VISIT_ID, opdTestConducted.getVisitId());
        contentValues.put(OpdDbConstants.Column.OpdTestConducted.CREATED_AT, opdTestConducted.getCreatedAt());
        contentValues.put(OpdDbConstants.Column.OpdTestConducted.UPDATED_AT, opdTestConducted.getUpdatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_TEST_CONDUCTED, null, contentValues);
        return rows != -1;
    }

    @Override
    public org.smartregister.opd.pojos.OpdTestConducted findOne(org.smartregister.opd.pojos.OpdTestConducted opdTestConducted) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(org.smartregister.opd.pojos.OpdTestConducted opdTestConducted) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<org.smartregister.opd.pojos.OpdTestConducted> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
