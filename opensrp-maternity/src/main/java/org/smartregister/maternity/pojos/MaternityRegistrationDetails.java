package org.smartregister.maternity.pojos;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegistrationDetails extends MaternityBaseDetails {

    // This enables us to easily change what is and can be saved
    public enum Property {
        gravidity,
        parity,
        lmp,
        gest_age,
        ga_weeks_entered,
        ga_days_entered,
        conception_date,
        onset_labour,
        previous_complications,
        previous_complications_other,
        previous_delivery_mode,
        previous_pregnancy_outcomes,
        family_history,
        family_history_other,
        hiv_status_previous,
        hiv_status_current,
        on_art_treatment,
        art_clinic_number,
        hiv_treatment_start,
        not_art_reason,
        not_art_reason_other,
        educ_level,
        marital_status,
        occupation,
        occupation_other,
        religion
    }

    public MaternityRegistrationDetails(@NonNull String baseEntityId, @NonNull Date eventDate, @NonNull HashMap<String, String> properties) {
        super(baseEntityId, eventDate, properties);
    }

}
