package org.smartregister.maternity.pojo;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityDetails extends MaternityBaseDetails {

    // This enables us to easily change what is and can be saved
    public enum Property {
        delivery_date,
        delivery_place,
        delivery_person,
        delivery_person_other,
        delivery_mode,
        baby_count_alive,
        baby_count_stillborn,
        mother_status,
        obstetric_complications,
        obstetric_complications_other,
        obstetric_care,
        obstetric_care_other,
        referred_out,
        vit_a,
        discharge_status,
    }

    public MaternityDetails(@NonNull String baseEntityId, @NonNull Date eventDate, @NonNull HashMap<String, String> properties) {
        super(baseEntityId, eventDate, properties);
    }
}
