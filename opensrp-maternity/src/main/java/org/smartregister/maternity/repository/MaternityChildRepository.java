package org.smartregister.maternity.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.smartregister.maternity.dao.MaternityGenericDao;
import org.smartregister.maternity.pojo.MaternityChild;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.BaseRepository;

import java.util.List;

public class MaternityChildRepository extends BaseRepository implements MaternityGenericDao<MaternityChild> {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.MATERNITY_BABY + "("
            + MaternityDbConstants.Column.MaternityBaby.MOTHER_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.MaternityBaby.APGAR + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.BF_FIRST_HOUR + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.FIRST_CRY + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.COMPLICATIONS + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.DISCHARGED_ALIVE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.DOB + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.COMPLICATIONS_OTHER + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.FIRST_NAME + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.LAST_NAME + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.GENDER + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.HEIGHT + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.WEIGHT + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.NVP_ADMINISTRATION + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.INTERVENTION_SPECIFY + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.INTERVENTION_REFERRAL_LOCATION + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityBaby.CARE_MGT + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.MATERNITY_BABY
            + "_" + MaternityDbConstants.Column.MaternityBaby.MOTHER_BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.MATERNITY_BABY +
            "(" + MaternityDbConstants.Column.MaternityBaby.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(MaternityChild maternityChild) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.MOTHER_BASE_ENTITY_ID, maternityChild.getMotherBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.APGAR, maternityChild.getApgar());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.BF_FIRST_HOUR, maternityChild.getBfFirstHour());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.COMPLICATIONS, maternityChild.getComplications());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.COMPLICATIONS_OTHER, maternityChild.getComplicationsOther());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.DISCHARGED_ALIVE, maternityChild.getDischargedAlive());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.DOB, maternityChild.getDob());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.FIRST_NAME, maternityChild.getFirstName());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.LAST_NAME, maternityChild.getLastName());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.HEIGHT, maternityChild.getHeight());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.WEIGHT, maternityChild.getWeight());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.CARE_MGT, maternityChild.getCareMgt());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.NVP_ADMINISTRATION, maternityChild.getNvpAdministration());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.GENDER, maternityChild.getGender());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.INTERVENTION_REFERRAL_LOCATION, maternityChild.getInterventionReferralLocation());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.INTERVENTION_SPECIFY, maternityChild.getInterventionSpecify());
        contentValues.put(MaternityDbConstants.Column.MaternityBaby.FIRST_CRY, maternityChild.getFirstCry());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.MATERNITY_BABY, null, contentValues);
        return rows != -1;
    }

    @Override
    public MaternityChild findOne(MaternityChild maternityChild) {
        throw new NotImplementedException("");
    }

    @Override
    public boolean delete(MaternityChild maternityChild) {
        throw new NotImplementedException("");
    }

    @Override
    public List<MaternityChild> findAll() {
        throw new NotImplementedException("");
    }

}
