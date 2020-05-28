package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.MaternityGenericDao;
import org.smartregister.maternity.pojo.MaternityStillBorn;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class MaternityStillBornRepository extends BaseRepository implements MaternityGenericDao<MaternityStillBorn> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.MATERNITY_STILL_BORN + "("
            + MaternityDbConstants.Column.MaternityStillBorn.MOTHER_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.MaternityStillBorn.STILLBIRTH__CONDITION + " VARCHAR NOT NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.MATERNITY_STILL_BORN
            + "_" + MaternityDbConstants.Column.MaternityStillBorn.MOTHER_BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.MATERNITY_STILL_BORN +
            "(" + MaternityDbConstants.Column.MaternityStillBorn.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    private String[] columns = new String[]{
            MaternityDbConstants.Column.MaternityStillBorn.MOTHER_BASE_ENTITY_ID,
            MaternityDbConstants.Column.MaternityStillBorn.STILLBIRTH__CONDITION
    };

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(MaternityStillBorn maternityStillBorn) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.MaternityStillBorn.MOTHER_BASE_ENTITY_ID, maternityStillBorn.getMotherBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.MaternityStillBorn.STILLBIRTH__CONDITION, maternityStillBorn.getStillBirthCondition());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.MATERNITY_STILL_BORN, null, contentValues);
        return rows != -1;
    }

    @Override
    public MaternityStillBorn findOne(MaternityStillBorn maternityStillBorn) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean delete(MaternityStillBorn maternityStillBorn) {
        throw new NotImplementedException("");
    }

    @Override
    public List<MaternityStillBorn> findAll() {
        throw new NotImplementedException("");
    }
}
