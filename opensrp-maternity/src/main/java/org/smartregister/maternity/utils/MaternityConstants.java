package org.smartregister.maternity.utils;

import org.smartregister.AllConstants;

public class MaternityConstants extends AllConstants {

    public static final String SEX = "Sex";
    public static final String CONFIG = "maternity_register";
    public static final String MOTHER = "mother";

    public interface IntentKey {
        String BASE_ENTITY_ID = "base-entity-id";
        String CLIENT_OBJECT = "common_person_object_client";
        String ENTITY_TABLE = "entity_table";
    }

    public interface Event {

        interface MaternityRegistration {

            String CONCEPTION_DATE = "conception_date";
            String PARA = "parity";
            String GRAVIDA = "gravidity";
            String PREVIOUS_HIV_STATUS = "hiv_status_previous";
            String CURRENT_HIV_STATUS = "hiv_status_current";
        }
    }

    public interface FactKey {

        interface ProfileOverview {
            String PREGNANCY_STATUS = "pregnancy_status";
            String GRAVIDA = "gravida";
            String PARA = "para";
            String GESTATION_WEEK = "gestation_week";
            String INTAKE_TIME = "intake_time";
            String HIV_STATUS = "hiv_status";
            String CURRENT_HIV_STATUS = "current_hiv_status";
        }
    }

    public interface JsonFormField {
        String MOTHER_HIV_STATUS = "mother_hiv_status";
    }

    public interface JsonFormWidget {
        String MULTI_SELECT_DRUG_PICKER = "multi_select_drug_picker";
    }

    public static class JSON_FORM_KEY {
        public static final String DEATH_DATE_APPROX = "deathdateApprox";
        public static final String OPTIONS = "options";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DOB = "dob";
        public static final String DOB_UNKNOWN = "dob_unknown";

        public static final String AGE_ENTERED = "age_entered";
        public static final String DOB_ENTERED = "dob_entered";
        public static final String HOME_ADDRESS_WIDGET_KEY = "home_address";
        public static final String VILLAGE_ADDRESS_WIDGET_KEY = "village";
        public static final String REMINDERS = "reminders";

        public static final String SERVICE_FEE = "service_fee";
        public static final String VISIT_ID = "visitId";
        public static final String DOSAGE = "dosage";
        public static final String DURATION = "duration";
        public static final String ID = "ID";
        public static final String VISIT_END_DATE = "visit_end_date";

        public static final String BHT_ID = "bht_mid";
        public static final String HOME_ADDRESS = "home_address";
        public static final String ENCOUNTER_TYPE = "encounter_type";
        public static final String ENTITY_ID = "entity_id";
        public static final String AGE = "age";
        public static final String MATERNITY_EDIT_FORM_TITLE = "Update Maternity Registration";
        public static final String FORM_TITLE = "title";
        public static final String OPENSRP_ID = "opensrp_id";
        public static final String BABIES_BORN = "babies_born";
        public static final String BABIES_STILLBORN = "babies_stillborn";
        public static final String DISCHARGED_ALIVE = "discharged_alive";
        public static final String ZEIR_ID = "zeir_id";
        public static final String BABIES_BORN_MAP = "BabiesBornMap";
        public static final String BABIES_STILL_BORN_MAP = "BabiesStillBornMap";


        public static final String BABY_COMPLICATIONS = "baby_complications";
        public static final String BABY_COMPLICATIONS_OTHER = "baby_complications_other";
        public static final String BABY_CARE_MGT = "baby_care_mgt";
        public static final String BABY_FIRST_CRY = "baby_first_cry";
        public static final String BABY_DOB = "baby_dob";
        public static final String BABY_FIRST_NAME = "baby_first_name";
        public static final String BABY_LAST_NAME = "baby_last_name";
        public static final String BABY_GENDER = "baby_gender";
        public static final String CHILD_HIV_STATUS = "child_hiv_status";
        public static final String BIRTH_HEALTH_ENTERED = "birth_height_entered";
        public static final String BIRTH_WEIGHT_ENTERED = "birth_weight_entered";
        public static final String NVP_ADMINISTRATION = "nvp_administration";
        public static final String BABY_INTERVENTION_SPECIFY = "baby_intervention_specify";
        public static final String BABY_INTERVENTION_REFERRAL_LOCATION = "baby_intervention_referral_location";
        public static final String BF_FIRST_HOUR = "bf_first_hour";
        public static final String APGAR = "apgar";
        public static final String STILLBIRTH_CONDITION = "stillbirth_condition";
        public static final String VILLAGE = "village";
        public static final String MMI_BASE_ENTITY_ID = "mmi_base_entity_id";
        public static final String SEX = "Sex";
        public static final String DATE_OF_DEATH = "date_of_death";
        public static final String DEATH_DATE = "deathdate";
        public static final String ATTRIBUTES = "attributes";
        public static final String DATE_REMOVED = "date_removed";
        public static final String MATERNITY_CLOSE_REASON = "maternity_close_reason";
    }

    public static class JSON_FORM_EXTRA {
        public static final String NEXT = "next";
        public static final String JSON = "json";
        public static final String ID = "id";
        public static final String WOMAN_DIED = "woman_died";
    }

    public static class JSON_FORM_STEP_NAME {
        public static final String BABIES_BORN = "Babies born";
        public static final String STILL_BORN_BABIES = "Still born babies";
    }


    public static class OPENMRS {
        public static final String ENTITY = "openmrs_entity";
        public static final String ENTITY_ID = "openmrs_entity_id";
    }

    public static final class KEY {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String PHOTO = "photo";
        public static final String LOOK_UP = "look_up";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String OPENSRP_ID = "opensrp_id";
        public static final String RELATIONALID = "relationalid";
        public static final String GENDER = "gender";
        public static final String DOD = "dod";
        public static final String DATE_REMOVED = "date_removed";
        public static final String MATERNITY_PARTIAL_FORM_ID = "mpf_id";
        public static final String MATERNITY_FORM_TYPE = "mpf_form_type";
    }

    public static class ENTITY {
        public static final String PERSON = "person";
    }

    public static class BOOLEAN_INT {
        public static final int TRUE = 1;
    }

    public static class FormActivity {
        public static final String EnableOnCloseDialog = "EnableOnCloseDialog";
    }

    public static final class EventType {
        public static final String MATERNITY_REGISTRATION = "Maternity Registration";
        public static final String UPDATE_MATERNITY_REGISTRATION = "Update Maternity Registration";
        public static final String MATERNITY_OUTCOME = "Maternity Outcome";
        public static final String MATERNITY_MEDIC_INFO = "Maternity Medic Information";
        public static final String MATERNITY_STILL_BORN = "Maternity Still Born";
        public static final String MATERNITY_BORN = "Maternity Born";
        public static final String MATERNITY_CLOSE = "Maternity Close";
        public static final String BIRTH_REGISTRATION = "Birth Registration";
        public static final String DEATH = "Death";
    }

    public interface ColumnMapKey {
        String REGISTER_ID = "register_id";
        String REGISTER_TYPE = "register_type";
        String PENDING_OUTCOME = "pending_outcome";
    }

    public interface DateFormat {
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    }

    public interface Form {
        String MATERNITY_REGISTRATION = "maternity_registration";
        String MATERNITY_OUTCOME = "maternity_outcome";
        String MATERNITY_MEDIC_INFO = "maternity_medic_info";
        String MATERNITY_CLOSE = "maternity_close";
    }

    public interface FormValue {
        String IS_DOB_UNKNOWN = "isDobUnknown";
        String IS_ENROLLED_IN_MESSAGES = "isEnrolledInSmsMessages";
        String OTHER = "other";
    }

    public interface RegisterType {
        String MATERNITY = "maternity";
    }

    public interface ClientMapKey {
        String GENDER = "gender";
    }

}
