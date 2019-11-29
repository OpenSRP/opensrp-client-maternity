package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.OpdServiceDetailDao;
import org.smartregister.maternity.pojos.OpdServiceDetail;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.List;

public class OpdServiceDetailRepository extends BaseRepository implements OpdServiceDetailDao {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.OPD_SERVICE_DETAIL + "("
            + MaternityDbConstants.Column.OpdServiceDetail.ID + " VARCHAR NOT NULL,"
            + MaternityDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdServiceDetail.FEE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdServiceDetail.DETAILS + " VARCHAR NULL, "
            + MaternityDbConstants.Column.OpdServiceDetail.VISIT_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.OpdServiceDetail.UPDATED_AT + " INTEGER NOT NULL, "
            + MaternityDbConstants.Column.OpdServiceDetail.CREATED_AT + " INTEGER NOT NULL ," +
            "UNIQUE(" + MaternityDbConstants.Column.OpdServiceDetail.ID + ") ON CONFLICT REPLACE)";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_SERVICE_DETAIL
            + "_" + MaternityDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.OPD_SERVICE_DETAIL +
            "(" + MaternityDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_VISIT_ID = "CREATE INDEX " + MaternityDbConstants.Table.OPD_SERVICE_DETAIL
            + "_" + MaternityDbConstants.Column.OpdServiceDetail.VISIT_ID + "_index ON " + MaternityDbConstants.Table.OPD_SERVICE_DETAIL +
            "(" + MaternityDbConstants.Column.OpdServiceDetail.VISIT_ID + " COLLATE NOCASE);";

//    private String[] columns = new String[]{
//            MaternityDbConstants.Column.OpdServiceDetail.ID,
//            MaternityDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID,
//            MaternityDbConstants.Column.OpdServiceDetail.FEE,
//            MaternityDbConstants.Column.OpdServiceDetail.DETAILS,
//            MaternityDbConstants.Column.OpdServiceDetail.VISIT_ID,
//            MaternityDbConstants.Column.OpdServiceDetail.UPDATED_AT,
//            MaternityDbConstants.Column.OpdServiceDetail.CREATED_AT
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
        contentValues.put(MaternityDbConstants.Column.OpdServiceDetail.ID, opdServiceDetail.getId());
        contentValues.put(MaternityDbConstants.Column.OpdServiceDetail.BASE_ENTITY_ID, opdServiceDetail.getBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.OpdServiceDetail.FEE, opdServiceDetail.getFee());
        contentValues.put(MaternityDbConstants.Column.OpdServiceDetail.DETAILS, opdServiceDetail.getDetails());
        contentValues.put(MaternityDbConstants.Column.OpdServiceDetail.VISIT_ID, opdServiceDetail.getVisitId());
        contentValues.put(MaternityDbConstants.Column.OpdServiceDetail.UPDATED_AT, opdServiceDetail.getUpdatedAt());
        contentValues.put(MaternityDbConstants.Column.OpdServiceDetail.CREATED_AT, opdServiceDetail.getCreatedAt());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.OPD_SERVICE_DETAIL, null, contentValues);
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