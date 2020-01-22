package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.OpdTestConductedDao;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class OpdTestConductedRepository extends BaseRepository implements OpdTestConductedDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.OPD_TEST_CONDUCTED + "("
            + MaternityDbConstants.Column.OpdTestConducted.ID + " VARCHAR NOT NULL,"
            + MaternityDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdTestConducted.TEST + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdTestConducted.RESULT + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdTestConducted.VISIT_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdTestConducted.UPDATED_AT + " INTEGER NOT NULL, "
            + MaternityDbConstants.Column.OpdTestConducted.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + MaternityDbConstants.Column.OpdTestConducted.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_TEST_CONDUCTED
            + "_" + MaternityDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.OPD_TEST_CONDUCTED +
            "(" + MaternityDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_TEST_CONDUCTED
            + "_" + MaternityDbConstants.Column.OpdTestConducted.VISIT_ID + "_index ON " + MaternityDbConstants.Table.OPD_TEST_CONDUCTED +
            "(" + MaternityDbConstants.Column.OpdTestConducted.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            MaternityDbConstants.Column.OpdTestConducted.ID,
//            MaternityDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID,
//            MaternityDbConstants.Column.OpdTestConducted.TEST,
//            MaternityDbConstants.Column.OpdTestConducted.RESULT,
//            MaternityDbConstants.Column.OpdTestConducted.VISIT_ID,
//            MaternityDbConstants.Column.OpdTestConducted.UPDATED_AT,
//            MaternityDbConstants.Column.OpdTestConducted.CREATED_AT
//    };

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }


    @Override
    public boolean saveOrUpdate(@NonNull org.smartregister.maternity.pojos.OpdTestConducted opdTestConducted) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.OpdTestConducted.ID, opdTestConducted.getId());
        contentValues.put(MaternityDbConstants.Column.OpdTestConducted.BASE_ENTITY_ID, opdTestConducted.getBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.OpdTestConducted.TEST, opdTestConducted.getTest());
        contentValues.put(MaternityDbConstants.Column.OpdTestConducted.RESULT, opdTestConducted.getResult());
        contentValues.put(MaternityDbConstants.Column.OpdTestConducted.VISIT_ID, opdTestConducted.getVisitId());
        contentValues.put(MaternityDbConstants.Column.OpdTestConducted.CREATED_AT, opdTestConducted.getCreatedAt());
        contentValues.put(MaternityDbConstants.Column.OpdTestConducted.UPDATED_AT, opdTestConducted.getUpdatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.OPD_TEST_CONDUCTED, null, contentValues);
        return rows != -1;
    }

    @Override
    public org.smartregister.maternity.pojos.OpdTestConducted findOne(org.smartregister.maternity.pojos.OpdTestConducted opdTestConducted) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(org.smartregister.maternity.pojos.OpdTestConducted opdTestConducted) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<org.smartregister.maternity.pojos.OpdTestConducted> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}
