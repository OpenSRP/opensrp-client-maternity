package org.smartregister.opd.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.opd.dao.OpdServiceDetailDao;
import org.smartregister.opd.pojos.OpdServiceDetail;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdServiceDetailRepository extends BaseRepository implements OpdServiceDetailDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + OpdDbConstants.Table.OPD_SERVICE_DETAIL + "("
            + OpdDbConstants.Column.OpdServiceDetail.ID + " VARCHAR NOT NULL,"
            + OpdDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdServiceDetail.FEE + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdServiceDetail.DETAILS + " VARCHAR NULL, "
            + OpdDbConstants.Column.OpdServiceDetail.VISIT_ID + " VARCHAR NOT NULL, "
            + OpdDbConstants.Column.OpdServiceDetail.UPDATED_AT + " INTEGER NOT NULL, "
            + OpdDbConstants.Column.OpdServiceDetail.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + OpdDbConstants.Column.OpdServiceDetail.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_SERVICE_DETAIL
            + "_" + OpdDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID + "_index ON " + OpdDbConstants.Table.OPD_SERVICE_DETAIL +
            "(" + OpdDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + OpdDbConstants.Table.OPD_SERVICE_DETAIL
            + "_" + OpdDbConstants.Column.OpdServiceDetail.VISIT_ID + "_index ON " + OpdDbConstants.Table.OPD_SERVICE_DETAIL +
            "(" + OpdDbConstants.Column.OpdServiceDetail.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            OpdDbConstants.Column.OpdServiceDetail.ID,
//            OpdDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID,
//            OpdDbConstants.Column.OpdServiceDetail.FEE,
//            OpdDbConstants.Column.OpdServiceDetail.DETAILS,
//            OpdDbConstants.Column.OpdServiceDetail.VISIT_ID,
//            OpdDbConstants.Column.OpdServiceDetail.UPDATED_AT,
//            OpdDbConstants.Column.OpdServiceDetail.CREATED_AT
//    };


    public OpdServiceDetailRepository(@NonNull Repository repository) {
        super(repository);
    }

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_VISIT_ID);
    }

    @Override
    public boolean saveOrUpdate(@NonNull OpdServiceDetail opdServiceDetail) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OpdDbConstants.Column.OpdServiceDetail.ID, opdServiceDetail.getId());
        contentValues.put(OpdDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID, opdServiceDetail.getBaseEntityId());
        contentValues.put(OpdDbConstants.Column.OpdServiceDetail.FEE, opdServiceDetail.getFee());
        contentValues.put(OpdDbConstants.Column.OpdServiceDetail.DETAILS, opdServiceDetail.getDetails());
        contentValues.put(OpdDbConstants.Column.OpdServiceDetail.VISIT_ID, opdServiceDetail.getVisitId());
        contentValues.put(OpdDbConstants.Column.OpdServiceDetail.UPDATED_AT, opdServiceDetail.getUpdatedAt());
        contentValues.put(OpdDbConstants.Column.OpdServiceDetail.CREATED_AT, opdServiceDetail.getCreatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(OpdDbConstants.Table.OPD_SERVICE_DETAIL, null, contentValues);
        return rows != -1;
    }

    @Override
    public OpdServiceDetail findOne(OpdServiceDetail opdServiceDetail) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public boolean delete(OpdServiceDetail opdServiceDetail) {
        throw new NotImplementedException("Not Implemented");
    }

    @Override
    public List<OpdServiceDetail> findAll() {
        throw new NotImplementedException("Not Implemented");
    }
}