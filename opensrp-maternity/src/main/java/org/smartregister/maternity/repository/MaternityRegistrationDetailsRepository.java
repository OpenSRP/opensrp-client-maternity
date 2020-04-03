package org.smartregister.maternity.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.pojos.MaternityRegistrationDetails;
import org.smartregister.maternity.utils.MaternityDbConstants;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegistrationDetailsRepository extends MaternityDetailsRepository {

    private String[] propertyNames;

    public static void createTable(@NonNull SQLiteDatabase database) {
        String CREATE_TABLE_SQL = "CREATE TABLE " + getTableNameStatic() + "("
                + MaternityDbConstants.Column.MaternityDetails.ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + " VARCHAR NOT NULL, "
                + MaternityDbConstants.Column.MaternityDetails.CREATED_AT + " DATETIME NOT NULL DEFAULT (DATETIME('now')), "
                + MaternityDbConstants.Column.MaternityDetails.EVENT_DATE + " DATETIME NOT NULL, ";

        for (MaternityRegistrationDetails.Property column: MaternityRegistrationDetails.Property.values()) {
            CREATE_TABLE_SQL += column.name() + " VARCHAR, ";
        }

        CREATE_TABLE_SQL += "UNIQUE(" + MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + ") ON CONFLICT REPLACE)";

        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL("CREATE INDEX " + MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + "_" + getTableNameStatic()
                + " ON " + getTableNameStatic() + " (" + MaternityDbConstants.Column.MaternityDetails.BASE_ENTITY_ID + ")");

        database.execSQL("CREATE INDEX " + MaternityDbConstants.Column.MaternityDetails.EVENT_DATE + "_" + getTableNameStatic()
                + " ON " + getTableNameStatic() + " (" + MaternityDbConstants.Column.MaternityDetails.EVENT_DATE + ")");
    }

    @NonNull
    private static String getTableNameStatic() {
        return MaternityDbConstants.Table.MATERNITY_REGISTRATION_DETAILS;
    }

    @Override
    public String getTableName() {
        return MaternityDbConstants.Table.MATERNITY_REGISTRATION_DETAILS;
    }

    @Override
    public String[] getPropertyNames() {
        if (propertyNames == null) {
            MaternityRegistrationDetails.Property[] properties = MaternityRegistrationDetails.Property.values();
            propertyNames = new String[properties.length];

            for (int i = 0; i < properties.length; i++) {
                propertyNames[i] = properties[i].name();
            }
        }

        return propertyNames;
    }
}
