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

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + MaternityDbConstants.Table.MATERNITY_CHILD + "("
            + MaternityDbConstants.Column.MaternityChild.MOTHER_BASE_ENTITY_ID + " VARCHAR NOT NULL, "
            + MaternityDbConstants.Column.MaternityChild.APGAR + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.BF_FIRST_HOUR + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.FIRST_CRY + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.COMPLICATIONS + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.DISCHARGED_ALIVE + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.DOB + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.COMPLICATIONS_OTHER + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.FIRST_NAME + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.LAST_NAME + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.GENDER + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.HEIGHT + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.WEIGHT + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.NVP_ADMINISTRATION + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.INTERVENTION_SPECIFY + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.INTERVENTION_REFERRAL_LOCATION + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.STILL_BIRTH_CONDITION + " VARCHAR NULL, "
            + MaternityDbConstants.Column.MaternityChild.CARE_MGT + " VARCHAR NULL,"
            + MaternityDbConstants.Column.MaternityChild.CHILD_HIV_STATUS + " VARCHAR NULL,"
            + MaternityDbConstants.Column.MaternityChild.EVENT_DATE + " VARCHAR NULL )";


    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + MaternityDbConstants.Table.MATERNITY_CHILD
            + "_" + MaternityDbConstants.Column.MaternityChild.MOTHER_BASE_ENTITY_ID + "_index ON " + MaternityDbConstants.Table.MATERNITY_CHILD +
            "(" + MaternityDbConstants.Column.MaternityChild.MOTHER_BASE_ENTITY_ID + " COLLATE NOCASE);";

    public static void createTable(@NonNull SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_BASE_ENTITY_ID);
    }

    @Override
    public boolean saveOrUpdate(MaternityChild maternityChild) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.MaternityChild.MOTHER_BASE_ENTITY_ID, maternityChild.getMotherBaseEntityId());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.APGAR, maternityChild.getApgar());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.BF_FIRST_HOUR, maternityChild.getBfFirstHour());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.COMPLICATIONS, maternityChild.getComplications());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.COMPLICATIONS_OTHER, maternityChild.getComplicationsOther());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.DISCHARGED_ALIVE, maternityChild.getDischargedAlive());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.DOB, maternityChild.getDob());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.FIRST_NAME, maternityChild.getFirstName());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.LAST_NAME, maternityChild.getLastName());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.HEIGHT, maternityChild.getHeight());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.WEIGHT, maternityChild.getWeight());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.CARE_MGT, maternityChild.getCareMgt());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.NVP_ADMINISTRATION, maternityChild.getNvpAdministration());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.GENDER, maternityChild.getGender());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.INTERVENTION_REFERRAL_LOCATION, maternityChild.getInterventionReferralLocation());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.STILL_BIRTH_CONDITION, maternityChild.getStillBirthCondition());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.FIRST_CRY, maternityChild.getFirstCry());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.CHILD_HIV_STATUS, maternityChild.getChildHivStatus());
        contentValues.put(MaternityDbConstants.Column.MaternityChild.EVENT_DATE, maternityChild.getEventDate());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long rows = sqLiteDatabase.insert(MaternityDbConstants.Table.MATERNITY_CHILD, null, contentValues);
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
