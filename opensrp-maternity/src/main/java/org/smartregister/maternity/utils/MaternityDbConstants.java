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
            String CREATED_AT = "created_at";
        }

        interface MaternityOutcomeForm {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String FORM = "form";
            String CREATED_AT = "created_at";
        }
    }

    interface Table {
        String EC_CLIENT = "ec_client";
        String MATERNITY_DETAILS = "maternity_details";
        String MATERNITY_OUTCOME_FORM = "maternity_outcome_form";
    }
}
