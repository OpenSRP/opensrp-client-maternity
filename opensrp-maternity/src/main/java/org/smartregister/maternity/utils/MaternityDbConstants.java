package org.smartregister.maternity.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface MaternityDbConstants {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    interface KEY {
        String ID = "_id";
        String FIRST_NAME = "first_name";
        String MIDDLE_NAME = "middle_name";
        String LAST_NAME = "last_name";
        String DOB = "dob";

        String REGISTER_ID = "register_id";
        String BASE_ENTITY_ID = "base_entity_id";
        String GA = "ga";
        String CONCEPTION_DATE = "conception_date";

        String TABLE = "ec_client";
        String OPENSRP_ID = "opensrp_id";
        String LAST_INTERACTED_WITH = "last_interacted_with";
        String DATE_REMOVED = "date_removed";
        String GEST_AGE = "gest_age";
        String GA_CALCULATED = "ga_calculated";
    }

    interface Column {

        interface Client {
            String ID = "_id";
            String PHOTO = "photo";
            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String BASE_ENTITY_ID = "base_entity_id";
            String DOB = "dob";
            String OPENSRP_ID = "opensrp_id";
            String RELATIONALID = "relationalid";
            String NATIONAL_ID = "national_id";
            String GENDER = "gender";
            String REGISTER_TYPE = "register_type";
        }

        interface MaternityDetails {
            String ID = "_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String PENDING_OUTCOME = "pending_outcome";
            String PARA = "para";
            String GRAVIDA = "gravida";
            String RECORDED_AT = "recorded_at";
            String CONCEPTION_DATE = "conception_date";
            String HIV_STATUS = "hiv_status";
            String EVENT_DATE = "event_date";
            String CREATED_AT = "created_at";
        }

        interface MaternityOutcomeForm {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String FORM = "form";
            String CREATED_AT = "created_at";
        }

        interface MaternityMedicInfoForm {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String FORM = "form";
            String CREATED_AT = "created_at";
        }

        interface MaternityChild {
            String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String DOB = "dob";
            String GENDER = "gender";
            String DISCHARGED_ALIVE = "discharged_alive";
            String WEIGHT = "weight";
            String HEIGHT = "height";
            String APGAR = "apgar";
            String FIRST_CRY = "first_cry";
            String COMPLICATIONS = "complications";
            String COMPLICATIONS_OTHER = "complications_other";
            String NVP_ADMINISTRATION = "nvp_administration";
            String BF_FIRST_HOUR = "bf_first_hour";
            String INTERVENTION_REFERRAL_LOCATION = "intervention_referral_location";
            String INTERVENTION_SPECIFY = "intervention_specify";
            String CARE_MGT = "care_mgt";
            String EVENT_DATE = "event_date";
            String STILL_BIRTH_CONDITION = "stillbirth_condition";
            String CHILD_HIV_STATUS = "child_hiv_status";
        }

    }

    interface Table {
        String EC_CLIENT = "ec_client";
        String MATERNITY_DETAILS = "maternity_details";
        String MATERNITY_REGISTRATION_DETAILS = "maternity_registration_details";
        String MATERNITY_OUTCOME_FORM = "maternity_outcome_form";
        String MATERNITY_MEDIC_INFO_FORM = "maternity_outcome_form";
        String MATERNITY_CHILD = "maternity_child";
    }
}
